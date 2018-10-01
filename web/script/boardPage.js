(function () {

    var refreshRate = 1000; //miliseconds
    var checkGameStartInterval;
    var updateDetailsInterval;
    var ajaxUpdateBoardInterval;
    var titleIdInterval;
    var isGameOver = false;
    var currentRoomId;
    var isGameStarted = false;
    var currentPlayerName;


    $(document).ready(function () { //DO NOT MOVE THIS FUNCTION
        currentRoomId = Cookies.get(roomid);
    });

    function makeUserOptions(requestType) {
        return {
            requestType: requestType,
            roomid: currentRoomId,
            organizer: Cookies.get(organizer)
        }
    }

    function ajaxUpdateBoard() {
        $.ajax({
            data: makeUserOptions("board"),
            url: gameURL,
            success: function (board) {
                updateBoard(board);
            }
        });
    }

    function updateBoard(board) {
        var rowsCount = board.rowsCount;
        var columnsCount = board.columnsCount;
        var playersCount = board.playersCount;
        var cells = board.cells;
        var playersDiscTypeMap = board.playersDiscTypeMap;

        for (var row = 1; row < rowsCount + 1; row++) {
            for (var col = 0; col < columnsCount; col++) {
                var td = $(document.getElementById("board").rows[row].getElementsByTagName("td")[col]);
                td.removeClass();
                td.addClass("waves-effect waves-light toggler");
                if(rowsCount < 14) {
                    td[0].innerText = "\n\n";
                } else {
                    td[0].innerText = "\n";
                }
                td[0].style.background = colors[parseInt(cells[col][row - 1])];
            }
        }

        var requestingPlayerName = Cookies.get(organizer);
        if (isGameStarted) {
            if (currentPlayerName === requestingPlayerName && Cookies.get(playerType) === "Computer") {
                $.ajax({
                    data: {
                        requestType: "computerTurn",
                        roomid: currentRoomId,
                        organizer: requestingPlayerName,
                    },
                    url: gameURL,
                    success: function (result) {
                        if ((typeof result) === "string"){
                            showMessage("Invalid Action", result, true);
                        } else {
                            updateBoard(result);
                        }
                    }
                });
            }

    }}

    $(document).ready(function ajaxBoard() {
        $.ajax({
            data: makeUserOptions("board"),
            url: gameURL,
            success: function (board) {
                createBoard(board);

                for (var column = 0; column < board.columnsCount; column++) {
                    $(document).on("click", "#top-btn-" + column, function (e) {
                        var idParts = e.currentTarget.id.split("-");
                        var id = idParts[idParts.length - 1];
                        doMove(parseInt(id) + 1, false);
                    });

                    $(document).on("click", "#bottom-btn-" + column, function (e) {
                        var idParts = e.currentTarget.id.split("-");
                        var id = idParts[idParts.length - 1];
                        doMove(parseInt(id) + 1, true);
                    });
                }
            }
        });
    });

    function doMove(column, isPopOut) {
        var requestingPlayerName = Cookies.get(organizer);
        if (isGameStarted) {
            if (currentPlayerName === requestingPlayerName) {
                $.ajax({
                    data: {
                        requestType: "turn",
                        column: column - 1,
                        roomid: currentRoomId,
                        organizer: requestingPlayerName,
                        isPopOut: isPopOut
                    },
                    url: gameURL,
                    success: function (result) {
                        if ((typeof result) === "string"){
                            showMessage("Invalid Action", result, true);
                        } else {
                            updateBoard(result);
                        }
                    }
                });
            }
            else {
                showMessage("Invalid Action", "Wait for your turn", true);
            }
        } else{
            showMessage("Invalid Action", "Game has not started yet: cannot make any move", true);
        }
    }

    $(document).ready(function () {
        $("#sidePanel *").addClass('disabled').prop('disabled', true);
    });

    function checkIfGameStarted() {
        $.ajax({
            data: makeUserOptions("checkGameStart"),
            url: gameURL,
            success: function (response) {
                updatePlayerList(response.second);
                isGameStarted = response.first;
                if (isGameStarted) { //if started
                    showMessage("Ninarow", "Game started");
                    $("#sidePanel *").removeClass('disabled').prop('disabled', false);
                    blinkTitleWithMessage("Game started");
                    clearInterval(checkGameStartInterval);

                    ajaxUpdateBoardInterval = setInterval(ajaxUpdateBoard, refreshRate);
                    updateDetailsInterval = setInterval(updateDetails, refreshRate);
                } else {
                    ajaxUpdateBoardInterval = setInterval(ajaxUpdateBoard, refreshRate);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState === 0) {
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
                if (response.isActivePlayer && Cookies.get("playerType") !== "Computer") {

                    if (response.isGameOver) {
                        $("#mainControl *").addClass('disabled').prop('disabled', true);
                    }
                    else {
                        $("#mainControl *").removeClass('disabled').prop('disabled', false);
                    }
                }
                else {
                    $("#controlPanel *").addClass('disabled').prop('disabled', true);
                }

                if (response.playerList.length === 1 && isGameStarted){
                    isGameOver = true;
                    alert("All other players left as chickens. You are the WINNER!");
                    handleResetGame();
                }

                $("#username").text("Username: " + Cookies.get(organizer));
                $("#roomid").text("Room ID: " + Cookies.get(roomid));
                $("#currPlayer").text("Current Player: " + response.currentPlayerName);
                currentPlayerName = response.currentPlayerName;

                updatePlayerList(response.playerList);

                if (response.isGameOver) { //if game over
                    handleGameOver();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState === 0) {
                }
            },
            timeout: 10000
        });
    }

    function updatePlayerList(playerList) {
        $("#userslist").empty();
        $.each(playerList || [], function (index, element) {
            var currIndicator = "";
            if (element.name === currentPlayerName){
                currIndicator = ">";
            }

            $('<tr>' +
                '<td>' + currIndicator + '</td>' +
                '<td align="center">' + createPlayerIcon(element.playerType) + '</td>' +
                '<td>' + element.name + '</td>' +
                '<td>' + element.turnsCount + '</td>' +
                '<td style="background: '+ element.discType + '"></td>' +
                '</tr>').appendTo($("#userslist"));
        });
    }

    function handleResetGame(){
        $.ajax({
            data: makeUserOptions("resetGame"),
            url: gameURL,
            success: function (response) {
                isGameOver = true;
                Cookies.remove(roomid);
                document.location.href = "rooms.html";
            }
        });
    }

    function handleGameOver() {
        $("#sidePanel *").addClass('disabled').prop('disabled', true);
        clearInterval(updateDetailsInterval);
        clearInterval(ajaxUpdateBoardInterval);
        $.ajax({
            data: makeUserOptions("systemMessage"),
            url: gameURL,
            success: function (response) {
                isGameOver = true;
                alert(response);
                handleResetGame();
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
                    document.location.href = response.redirect;
                } else {
                    showMessage("Game Over", response, false);
                }
            }
        });
    });

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

}());