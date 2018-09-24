/**
 * Created by s on 18/10/2016.
 */

const organizer = "organizer";
const spectator = "spectator";
const roomid = "roomid";
const playerType = "playerType";
const requestType = "requestType";
const roomsURL = "rooms";
const gameURL = "game";

function createPlayerIcon(usertype){
    if(usertype == "Human"){
        return '<img src="images/person.png" class="img" alt="Human" width="30" height="30" style="box-shadow: none;">';
    }
    else if(usertype == "Computer"){
        return '<img src="images/robot.png" class="img" alt="Computer" width="30" height="30" style="box-shadow: none;">';
    }
}

function createBoard(board) {
    var table = $("#board");
    table.addClass("board");

    var cells = board.cells;
    var columnsCount = board.columnsCount;
    var rowsCount = board.rowsCount;

    for (var row = 0; row < rowsCount; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columnsCount; column++) {
            var td = document.createElement('td');
            td.classList.add("block");
            td.innerText = cells[row][column];
            tr.appendChild(td);
        }
        table.append(tr);
    }

}


function getMaxLengthOfList(list) {
    var retValue = 0;
    for (var i = 0; i < list.length; i++) {
        if (list[i].length > retValue) {
            retValue = list[i].length;
        }
    }
    return retValue;
}
