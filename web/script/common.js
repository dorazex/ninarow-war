const organizer = "organizer";
const roomid = "roomid";
const playerType = "playerType";
const requestType = "requestType";
const roomsURL = "rooms";
const gameURL = "game";
const colors = ["#bebebe", "#82b74b", "#034f84", "#6b5b95", "#feb236", "#d64161", "#ff7b25"];

function createPlayerIcon(usertype){
    if(usertype == "Human"){
        return '<img src="images/baby.png" class="img" alt="Human" width="30" height="30" style="box-shadow: none;">';
    }
    else if(usertype == "Computer"){
        return '<img src="images/comp.png" class="img" alt="Computer" width="30" height="30" style="box-shadow: none;">';
    }
}

function createBoard(board) {
    var table = $("#board");
    table.addClass("board");
    var tableWidth = parseInt(table.width());
    var cells = board.cells;

    var columnsCount = board.columnsCount;
    var rowsCount = board.rowsCount;

    var calculatedColumnWidth = Math.round((tableWidth - 50)/ columnsCount);

    // create top buttons
    var tr = document.createElement('tr');
    for (var column = 0; column < columnsCount; column++) {
        var td = document.createElement('td');
        var button = document.createElement('button')
        button.classList.add("btn");
        button.classList.add("btn-default");
        button.classList.add("btn-sm");
        button.classList.add("waves-effect");
        button.classList.add("waves-light");
        button.innerText = "v";
        button.setAttribute("id", "top-btn-" + column);
        button.setAttribute("style", "width:" + calculatedColumnWidth.toString());
        td.classList.add("waves-effect")
        td.classList.add("waves-light")
        td.classList.add("toggler")
        td.appendChild(button);
        tr.appendChild(td);
    }
    table.append(tr);

    // create board
    for (var row = 0; row < rowsCount; row++) {
        var tr = document.createElement('tr');
        for (var column = 0; column < columnsCount; column++) {
            var td = document.createElement('td');
            td.classList.add("block");
            if (cells[column][row] === undefined){
                console.log("column:" + column);
                console.log("row:" + row);
            }
            td.innerText = "\n\n";
            td.setAttribute("style", "background:" + colors[parseInt(cells[column][row])])
            tr.appendChild(td);
        }
        table.append(tr);
    }

    if (board.isPopOut === true) {
        // create bottom buttons
        var tr = document.createElement('tr');
        for (var column = 0; column < columnsCount; column++) {
            var td = document.createElement('td');
            var button = document.createElement('button')
            button.classList.add("btn");
            button.classList.add("btn-default");
            button.classList.add("btn-sm");
            button.classList.add("waves-effect");
            button.classList.add("waves-light");
            button.innerText = "^";
            button.setAttribute("id", "bottom-btn-" + column);
            button.setAttribute("style", "width:" + calculatedColumnWidth.toString());
            td.classList.add("waves-effect")
            td.classList.add("waves-light")
            td.classList.add("toggler")
            td.appendChild(button);
            tr.appendChild(td);
        }
        table.append(tr);
    }

}