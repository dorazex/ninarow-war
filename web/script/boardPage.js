/**
 * Created by moran on 10/13/2016.
 */

(function () {

    var refreshRate = 1000; //miliseconds
    var checkGameStartInterval;
    var updateDetailsInterval;
    var ajaxUpdateBoardInterval;
    var titleIdInterval;
    var isGameOver = false;
    var reviewOffset = 0;
    var isReplayMode = false;
    var currentRoomId;  // this enables for the same session to be in different rooms

    function Coordinate(row, column) {
        this.key = row;
        this.value = column;
    }

    $(document).ready(function () { //DO NOT MOVE THIS FUNCTION
        currentRoomId = Cookies.get(roomid);
    });

    function makeUserOptions(requestType) {
        if (Cookies.get(spectator)) {
            return {
                requestType: requestType,
                roomid: currentRoomId,
                spectator: true
            }
        }
        else {
            return {
                requestType: requestType,
                roomid: currentRoomId,
                organizer: Cookies.get(organizer)
            }
        }
    }

//region board

    function expandPageWidthAccordingToBoard(width) {

        var widthOfBlock = $(".block").width() + 2;
        var actualWidthOfBoard = widthOfBlock * width;
        var widthOfControl = $("#controlPanel").width();

        if (actualWidthOfBoard + widthOfControl > 970) {
            $(".container").css("width", "100%");    //increase the size of all the containers for a larger board
        }
    }

    function ajaxUpdateBoard() {
        $.ajax({
            data: makeUserOptions("board"),
            url: gameURL,
            success: function (board) {
                updateBoard(board);
                updatePerfectBlock(board.rowsBlocks, board.columnsBlocks);
            }
        });
    }

    function updateBoard(board) {
        var theBoard = board.board;
        var rowsBlocks = board.rowsBlocks;
        var columnsBlocks = board.columnsBlocks;
        var rows = theBoard.length;
        var columns = theBoard[0].length;
        var maxColumnBlock = getMaxLengthOfList(columnsBlocks);
        var maxRowBlock = getMaxLengthOfList(rowsBlocks);

        for (var row = 0; row < rows; row++) {
            for (var col = 0; col < columns; col++) {
                $(document.getElementById("board").rows[row + maxColumnBlock].getElementsByTagName("td")[col + maxRowBlock])
                    .removeClass()
                    .addClass("waves-effect waves-light toggler")
                    .addClass(theBoard[row][col].toLowerCase());
            }
        }
    }

    function updatePerfectBlock(rowsBlocks, columnsBlocks) {
        updatePerfectBlockRowsColumns(rowsBlocks, rowsBlocks.length, true, getMaxLengthOfList(columnsBlocks));
        updatePerfectBlockRowsColumns(columnsBlocks, columnsBlocks.length, false, getMaxLengthOfList(rowsBlocks));
    }

    function updatePerfectBlockRowsColumns(blocks, heigth, isRows, offset) {
        var maxBlock = getMaxLengthOfList(blocks);

        for (var i = 0; i < heigth; i++) {
            var x = maxBlock - 1;
            var currentBlocks = blocks[i];
            for (var j = currentBlocks.length - 1; j >= 0; j--) {
                if (currentBlocks[j].value == true) {
                    if (isRows == true) {
                        $('#board').find('tr').eq(i + offset).find('td').eq(x--).addClass('perfected');
                    }
                    else {
                        $('#board').find('tr').eq(x--).find('td').eq(i + offset).addClass('perfected');
                    }
                }
                else {
                    if (isRows == true) {
                        $('#board').find('tr').eq(i + offset).find('td').eq(x--).removeClass('perfected');
                    }
                    else {
                        $('#board').find('tr').eq(x--).find('td').eq(i + offset).removeClass('perfected');
                    }
                }
            }
        }
    }

    $(document).on("click", "td.toggler", function () {
        $(this).toggleClass('selected');
    });

    $(document).ready(function ajaxBoard() {

        $.ajax({
            data: makeUserOptions("board"),
            url: gameURL,
            success: function (board) {
                createBoard(board);
                expandPageWidthAccordingToBoard(board.board[0].length + getMaxLengthOfList(board.rowsBlocks));
            }
        });
    });

//endregion

//region game controls

    function doMove(move) {
        var selectedCoords = [];
        $("#board").find("td.toggler.selected").each(function () {
            var row = $(this).attr("row");
            var column = $(this).attr("column");
            selectedCoords.push(new Coordinate(row, column));
        });

        $.ajax({
            data: {
                requestType: move,
                "selectedCoords": JSON.stringify(selectedCoords),
                roomid: currentRoomId,
                organizer: Cookies.get(organizer),
            },
            url: gameURL,
            success: function (result) {
                if (result.isSuccessful) {    //we already have the list, so just verify it was successful
                    $("#board").find("td.toggler.selected").each(function () {
                        $(this).toggleClass('selected');
                        $(this).removeClass(this.className.split(' ').pop());
                        $(this).addClass(move);
                    });
                }
            }
        });
    }


    function handleUndoRedo(undoOrRedo) {
        removeSelectedSquares();

        $.ajax({
            data: makeUserOptions(undoOrRedo),
            url: gameURL,
            success: function (response) {
                var moves = response;
                var lenMoves = moves.length;
                for (i = 0; i < lenMoves; i++) {
                    var sign = moves[i].third;
                    var td = $('[row="' + moves[i].first + '"][column="' + moves[i].second + '"]');
                    td.removeClass(td.attr('class').split(' ').pop());
                    td.addClass(moves[i].third.toLowerCase());
                }
            }
        });
    }

    function removeSelectedSquares() {
        $("#board").find("td.toggler.selected").each(function () {
            $(this).toggleClass('selected');
        });
    }

    function doTurnDone(move) {

        $.ajax({
            data: makeUserOptions("turnDone"),
            url: gameURL,
            success: function (response) {
                if (response.isSuccessful) {
                    $("#controlPanel *").addClass('disabled').prop('disabled', true);
                    removeSelectedSquares();
                }
            }
        });
    }

    $(document).on("click", "#undo", function (e) {
        handleUndoRedo("undo");
    });

    $(document).on("click", "#redo", function (e) {
        handleUndoRedo("redo");
    });

    $(document).on("click", "#empty", function (e) {
        doMove("empty");
    });

    $(document).on("click", "#filled", function (e) {

        doMove("filled");
    });

    $(document).on("click", "#unknown", function (e) {
        doMove("unknown");
    });

    $(document).on("click", "#turnDone", function (e) {
        doTurnDone("unknown");
    });

    $(document).ready(function () {
        $("#sidePanel *").addClass('disabled').prop('disabled', true);
    });

//endregion

//region polling

    function checkIfGameStarted() {
        $.ajax({
            data: makeUserOptions("checkGameStart"),
            url: gameURL,
            success: function (response) {
                updatePlayerList(response.value);

                if (response.key) { //if started
                    showMessage("Nonogram", "Game started");
                    $("#sidePanel *").removeClass('disabled').prop('disabled', false);
                    blinkTitleWithMessage("Game started");
                    clearInterval(checkGameStartInterval);

                    if (Cookies.get(spectator)) {
                        $("#sidePanel *").removeClass('disabled').prop('disabled', true);
                        ajaxUpdateBoardInterval = setInterval(ajaxUpdateBoard, refreshRate);
                        updateDetailsInterval = setInterval(updateDetailsSpectator, refreshRate);
                        return;
                    }

                    else if (Cookies.get("playerType") == "Computer") {
                        ajaxUpdateBoardInterval = setInterval(ajaxUpdateBoard, refreshRate);
                    }

                    updateDetailsInterval = setInterval(updateDetails, refreshRate);    // relevant to player, not spectator
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState == 0) {
                    showMessage("Error", "Lost connection with server", true);
                }
            },
            timeout: 10000
        });
    }

//activate the timer calls after the page is loaded
    $(document).ready(function () {
        //The users list is refreshed automatically
        checkGameStartInterval = setInterval(checkIfGameStarted, refreshRate);
    });

    function updateDetails() {
        $.ajax({
            data: makeUserOptions("gameDetails"),
            url: gameURL,
            success: function (response) {
                if (response.isActivePlayer && Cookies.get("playerType") != "Computer") {
                    if(isReplayMode == false){ //remove disable only if not in replay
                        $("#sidePanel *").removeClass('disabled').prop('disabled', false);
                    }
                    $("#moves").text("Moves: " + response.numOfTurns + "/2");
                    updatePerfectBlock(response.rowsBlocks, response.columnsBlocks);

                    if (response.numOfTurns == 2) {
                        $("#mainControl *").addClass('disabled').prop('disabled', true);
                    }
                    else {
                        $("#mainControl *").removeClass('disabled').prop('disabled', false);
                    }
                }
                else {
                    $("#controlPanel *").addClass('disabled').prop('disabled', true);
                }

                if (isReplayMode == true) {
                    $("#controlPanel *").addClass('disabled').prop('disabled', true);
                }

                $("#rounds").text("Rounds: " + response.currentRound + "/" + response.totalRounds);
                $("#username").text("Username: " + Cookies.get(organizer));
                $("#roomid").text("Room ID: " + Cookies.get(roomid));
                $("#currPlayer").text("Current Player: " + response.currentPlayerName);

                updatePlayerList(response.playerList);

                if (response.isGameOver) { //if game over
                    handleGameOver();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState == 0) {
                    showMessage("Error", "Lost connection with server", true);
                }
            },
            timeout: 10000
        });
    }

    function updateDetailsSpectator() {
        $.ajax({
            data: {
                requestType: "gameDetails",
                roomid: currentRoomId
            },
            url: gameURL,
            success: function (response) {

                $("#moves").text("Moves: " + response.numOfTurns + "/2");
                $("#rounds").text("Rounds: " + response.currentRound + "/" + response.totalRounds);
                $("#username").text("Username: " + Cookies.get(organizer));
                $("#roomid").text("Room ID: " + Cookies.get(roomid));
                $("#currPlayer").text("Current Player: " + response.currentPlayerName);

                updatePlayerList(response.playerList);

                if (response.isGameOver) { //if game over
                    handleGameOver();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState == 0) {
                    showMessage("Error", "Lost connection with server", true);
                }
            },
            timeout: 10000
        });
    }

    function updatePlayerList(playerList) {
        //g(playerList);
        $("#userslist").empty();
        $.each(playerList || [], function (index, element) {
            console.log(element.first);
            if (element.first.toLowerCase() == spectator) {
                $('<tr>' +
                    '<td align="center">' + createPlayerIcon(element.first) + '</td>' +
                    '<td>' + element.second + '</td>' +
                    '<td>' + "-" + '</td>' +
                    '</tr>').appendTo($("#userslist"));
            }
            else {
                $('<tr>' +
                    '<td align="center">' + createPlayerIcon(element.first) + '</td>' +
                    '<td>' + element.second + '</td>' +
                    '<td>' + element.third + '</td>' +
                    '</tr>').appendTo($("#userslist"));
            }

        });
    }

//endregion

//region end game

    function handleGameOver() {
        $("#sidePanel *").addClass('disabled').prop('disabled', true);
        clearInterval(updateDetailsInterval);
        clearInterval(ajaxUpdateBoardInterval);
        $.ajax({
            data: makeUserOptions("systemMessage"),
            url: gameURL,
            success: function (response) {
                showMessage("Game Over", response);
                blinkTitleWithMessage("Game Over");
                isGameOver = true;
            }
        });
    }

    $(document).on("click", "#leaveRoom", function (e) {
        $.ajax({
            data: makeUserOptions("leaveRoom"),
            url: gameURL,
            success: function (response) {
                if (typeof response.redirect !== "undefined") {
                    Cookies.remove(roomid);
                    Cookies.remove(spectator);
                    document.location.href = response.redirect;
                }
            }
        });
    });

//endregion

//region ui related
    function showMessage(title, message, isError) {

        if (isError) {
            $('.modal-header').css('background-color', '#ff4444');
        }
        else {
            $('.modal-header').css('background-color', '#00C851');
        }

        $('#modalTitle').text(title);
        $('#modalMessage').text(message);

        if ($('#messageModal').is(':hidden')) {
            $('#messageModal').modal('show');
        }
    }

    function blinkTitleWithMessage(message) {
        var oldTitle = document.title;
        var msg = message;

        var blink = function () {
            document.title = document.title == msg ? ' ' : msg;
        };
        var clear = function () {
            clearInterval(titleIdInterval);
            document.title = oldTitle;
            window.onmousemove = null;
            titleIdInterval = null;
        };
        clearInterval(titleIdInterval);
        titleIdInterval = setInterval(blink, 1000);
        window.onmousemove = clear;
    }

//endregion

//region replay related

    $(document).on("click", "#replay", function (e) {
        $(this).toggleClass('info');
        removeSelectedSquares();
        if ($('#replay').hasClass('info')) {
            isReplayMode = true;
            $("#controlPanel *").addClass('disabled').prop('disabled', true);
            $('#replay').text("Return");
            document.getElementById("previous").style.visibility = "visible";
            document.getElementById("next").style.visibility = "visible";
            $("#next").addClass('disabled').prop('disabled', true);
            decideDisableNextOrPrevButtons();
        }
        else {
           restoreBoardAfterReplay();
        }
    });

    function restoreBoardAfterReplay() {
        isReplayMode = false;
        $('#replay').text("Replay");
        document.getElementById("previous").style.visibility = "hidden";
        document.getElementById("next").style.visibility = "hidden";
        $("#previous").removeClass('disabled').prop('disabled', false);
        $("#next").removeClass('disabled').prop('disabled', false);
        $.ajax({
            data: {
                requestType: "resetToLastMove",
                roomid: currentRoomId,
                organizer: Cookies.get(organizer),
                "reviewOffset": reviewOffset
            },
            url: gameURL,
            success: function () {
                ajaxUpdateBoard();
                reviewOffset = 0;
            }
        });
    }

    function decideDisableNextOrPrevButtons() {
        $.ajax({
            data: makeUserOptions("numOfAllMoves"),
            url: gameURL,
            success: function (response) {
                if (response == 0)
                    $("#previous").addClass('disabled').prop('disabled', true);
                else
                    $("#previous").removeClass('disabled').prop('disabled', false);
                $("#next").addClass('disabled').prop('disabled', true);
            }
        });
    }

    $(document).on("click", "#previous", function (e) {
        ajaxMoveWithoutChangeState("undoMoveWithoutChangeState");
        reviewOffset--;
        $.ajax({
            data: makeUserOptions("numOfAllMoves"),
            url: gameURL,
            success: function (response) {
                if (response + reviewOffset == 0)
                    $("#previous").addClass('disabled').prop('disabled', true);
                $("#next").removeClass('disabled').prop('disabled', false);
            }
        });
    });

    $(document).on("click", "#next", function (e) {
        ajaxMoveWithoutChangeState("redoMoveWithoutChangeState");
        reviewOffset++;
        if (reviewOffset == 0)
            $("#next").addClass('disabled').prop('disabled', true);
        $("#previous").removeClass('disabled').prop('disabled', false);
    });

    function ajaxMoveWithoutChangeState(move) {
        $.ajax({
            data: makeUserOptions(move),
            url: gameURL,
            success: function () {
                ajaxUpdateBoard();
            }
        });
    }

//endregion

}());