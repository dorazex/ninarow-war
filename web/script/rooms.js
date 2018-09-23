(function () {
//region polling

    var refreshRate = 1000; //miliseconds

    function refreshUsersList(users) {  //TODO change this to app/json

        //clear all current users
        $("#userslist").empty();
        $.each(users || [], function (index, element) {

            $('<tr>' +
                '<td align="center">' + createPlayerIcon(element.playerType) + '</td>' + //TODO make icons
                '<td>' + element.name + '</td>' +
                '</tr>').appendTo($("#userslist"));
        });
    }

    function refreshRoomsList(rooms) {




        var selected = $("#roomslist").find("tr.info").attr('id');
        //clear all current rooms
        $("#roomslist").empty();

        $.each(rooms || [], function (index, element) {

            $('<tr id=' + element.roomIdentifier + '>' +
                '<td>' + element.gameTitle + '</td>' +
                '<td>' + element.organizer + '</td>' +
                '<td>' + element.onlinePlayers + "/" + element.totalPlayers + '</td>' +
                '<td>' + '<i class="fa fa-eye" aria-hidden="true">' + element.spectators + '</i>' + '</td>' +
                '<td>' + element.rounds + '</td>' +
                '<td>' + element.rows + "X" + element.columns + '</td>' +
                '</tr>').appendTo($("#roomslist"));

            if (element.roomIdentifier == selected) {
                $('#' + selected.toString()).addClass('info');
            }
        });
    }

    function ajaxUsersList() {
        $.ajax({
            data: {requestType: "userList"},
            url: roomsURL,
            success: function (users) {
                refreshUsersList(users);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState == 0) {
                    showErrorModal("Lost connection with server");
                }
            },
            timeout: 10000
        });
    }

    function ajaxRoomsList() {
        $.ajax({
            data: {requestType: "roomList"},
            url: roomsURL,
            success: function (rooms) {
                refreshRoomsList(rooms);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (textStatus === "timeout") {
                    showMessage("Timeout", "No connection", true);
                }
                else if (XMLHttpRequest.readyState == 0) {
                    showErrorModal("Lost connection with server");
                }
            },
            timeout: 10000
        });
    }

    function refreshPageData() {
        ajaxUsersList();
        ajaxRoomsList();
    }

//activate the timer calls after the page is loaded
    $(document).ready(function () {
        setInterval(refreshPageData, refreshRate);
    });

//endregion

//region buttons

// click button to enter a room
    $(document).on("click", "#enterRoom", function (e) {
        var selected = $("#roomslist").find("tr.info");
        if (selected != undefined) {
            selected = $("#roomslist").find("tr.info").attr('id');
            $.ajax({
                data: {
                    organizer: Cookies.get(organizer),
                    playerType: Cookies.get(playerType),
                    requestType: "enterRoom",
                    roomid: selected
                },
                url: roomsURL,
                success: function (responseJson) {  //change to board page
                    if (typeof responseJson.redirect !== "undefined") {
                        Cookies.remove("spectator");
                        document.location.href = responseJson.redirect;
                    }
                    else if (typeof responseJson.error !== "undefined") {
                        showErrorModal(responseJson.error);
                    }
                }
            });
        }
    });

    $(document).on("click", "#roomslist tr", function (e) {
        $(this).addClass('info').siblings().removeClass('info');
    });

    $(document).on("click", "#uploadbutton", function (e) {
        if (document.getElementById("file").files[0]) {  //if a file was chosen
            var formData = new FormData();
            formData.append("XMLFile", document.getElementById("file").files[0]);
            formData.append(requestType, "fileUpload");
            formData.append(organizer, Cookies.get(organizer));
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (xhr.responseText) {   //if there's something, it's an error
                    showErrorModal(xhr.responseText);
                }
            };
            xhr.open("POST", roomsURL, true);
            xhr.send(formData);
        }
        else {
            showErrorModal("No file was chosen");
        }
    });

    $(document).on("click", "#logout", function (e) {
        console.log("logout");
        $.ajax({
            data: {
                requestType: "logout",
                organizer: Cookies.get(organizer)
            },
            url: "rooms",
            success: function (response) {
                //go back to login page
                if (typeof response.redirect !== "undefined") {
                    Cookies.remove(organizer);
                    Cookies.remove(playerType);
                    // Cookies.remove(organizer, {path: window.location.pathname.split( '/' )[1]});
                    // Cookies.remove(playerType, {path: window.location.pathname.split( '/' )[1]});
                    document.location.href = response.redirect;
                }
            }
        });
    });

    $(document).on("click", "#spectateRoom", function (e) {
        var selected = $("#roomslist").find("tr.info");
        if (selected) {
            selected = $("#roomslist").find("tr.info").attr('id');
            $.ajax({
                data: {
                    spectator: Cookies.get(organizer),
                    requestType: "spectateRoom",
                    roomid: selected
                },
                url: roomsURL,
                success: function (response) {
                    //open a window with the board
                    if (typeof response.redirect !== "undefined") {
                        document.location.href = response.redirect;
                    }
                    else if (response.error) {
                        showErrorModal(response.error);
                    }
                }
            });
        }
    });

    $(document).on("click", "#viewBoard", function (e) {

        var selected = $("#roomslist").find("tr.info");
        if (selected) {
            selected = $("#roomslist").find("tr.info").attr('id');
            $.ajax({
                data: {
                    requestType: "board",
                    roomid: selected
                },
                url: gameURL,
                success: function (response) {
                    //open a window with the board
                    $('#board').empty();
                    createBoard(response);
                    $('#boardModal').modal('show');
                }
            });
        }
    });

//endregion

    function showErrorModal(message) {
        $('#errorMessage').text(message);
        if ($('#errorModal').is(':hidden')) {
            $('#errorModal').modal('show');
        }
    }

}());