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
    else if(usertype == "Spectator"){
        return '<img src="images/eye.png" class="img" alt="Spectator" width="30" height="30" style="box-shadow: none;">';
    }
}

function createBoard(theBoard) {
    var table = $("#board");
    table.addClass("board");
    createTopPart(theBoard, table);
    createDownPart(theBoard, table);
}

function createTopPart(board, table) {
    var theBoard = board.board;
    var columns = theBoard[0].length;
    var columnsBlocks = board.columnsBlocks;
    var maxRowBlock = getMaxLengthOfList(board.rowsBlocks);
    var maxColumnBlock = getMaxLengthOfList(columnsBlocks);

    for (var row = 0; row < maxColumnBlock; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columns + maxRowBlock; column++) {
            var td = document.createElement('td');
            td.classList.add("block");
            if (column >= maxRowBlock && maxColumnBlock - row <= columnsBlocks[column - maxRowBlock].length) {
                td.innerText = columnsBlocks[column - maxRowBlock][columnsBlocks[column - maxRowBlock].length - maxColumnBlock + row].key.toString();
            }
            tr.appendChild(td);
        }
        table.append(tr);
    }
}

function createDownPart(board, table) {
    var theBoard = board.board;
    var rowsBlocks = board.rowsBlocks;
    var columnsBlocks = board.columnsBlocks;
    var rows = theBoard.length;
    var columns = theBoard[0].length;
    var maxRowBlock = getMaxLengthOfList(rowsBlocks);

    for (var row = 0; row < rows; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columns + maxRowBlock; column++) {
            var td = document.createElement('td');
            td.setAttribute("row", row);
            td.setAttribute("column", (column - maxRowBlock));
            if (column < maxRowBlock && maxRowBlock - column <= rowsBlocks[row].length) {
                td.classList.add("block");
                td.innerText = rowsBlocks[row][rowsBlocks[row].length - maxRowBlock + column].key;
            }
            else if (column >= maxRowBlock) {   // add class according to real board
                td.classList.add("waves-effect", "waves-light", "toggler");
                td.classList.add(theBoard[row][column - maxRowBlock].toLowerCase());
            }
            else {
                td.classList.add("block");
            }
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
