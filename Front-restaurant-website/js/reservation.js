const API_URL = "http://192.168.1.70:8080"; // Убедитесь, что URL доступен из браузера
const tableGrid = document.getElementById('tableGrid');
const reservationForm = document.getElementById('reservation-form');
const numTables = 20; // Количество столиков

// Данные о столиках с максимальным количеством мест
const tablesInfo = [
    { id: 1, seats: 4 }, { id: 2, seats: 2 }, { id: 3, seats: 6 },
    { id: 4, seats: 4 }, { id: 5, seats: 3 }, { id: 6, seats: 5 },
    { id: 7, seats: 2 }, { id: 8, seats: 6 }, { id: 9, seats: 4 },
    { id: 10, seats: 5 }, { id: 11, seats: 3 }, { id: 12, seats: 2 },
    { id: 13, seats: 6 }, { id: 14, seats: 4 }, { id: 15, seats: 5 },
    { id: 16, seats: 4 }, { id: 17, seats: 6 }, { id: 18, seats: 3 },
    { id: 19, seats: 2 }, { id: 20, seats: 5 }
];

const tableData = [];

// Создаем столики
tablesInfo.forEach((tableInfo) => {
    const table = document.createElement('div');
    table.classList.add('table');
    table.id = `table-${tableInfo.id}`;  // Уникальный ID для каждого столика

    const title = document.createElement('div');
    title.classList.add('title');
    title.textContent = `Столик ${tableInfo.id}`;

    const seatInfo = document.createElement('div');
    seatInfo.classList.add('seat-info');
    seatInfo.textContent = `Макс. ${tableInfo.seats} чел.`;

    table.appendChild(title);
    table.appendChild(seatInfo);

    const data = {
        element: table,
        id: tableInfo.id,
        seats: tableInfo.seats,
        reserved: false // Изначально столик свободен
    };

    table.addEventListener('click', () => {
        toggleTableSelection(data); // Выбор столика при клике
    });

    tableData.push(data);
    tableGrid.appendChild(table);
});

// Функция для выбора столика (или отмены выбора)
function toggleTableSelection(data) {
    if (data.element.classList.contains('selected')) {
        // Если столик уже выбран, сбрасываем выбор
        data.element.classList.remove('selected');
        document.getElementById('tableId').value = ''; // Очищаем ID в форме
    } else {
        // Если столик не выбран, выбираем его
        document.querySelectorAll('.table').forEach(table => table.classList.remove('selected')); // Снимаем выбор с других столиков
        data.element.classList.add('selected');
        document.getElementById('tableId').value = data.id; // Подставляем ID в форму
    }
}


// Обработчик отправки формы
reservationForm.addEventListener('submit', async function(event) {
    event.preventDefault();

    const token = localStorage.getItem('token');
    if (!token) {
        alert('Ошибка: Вы не авторизованы. Пожалуйста, войдите в систему.');
        return;
    }

    const people = parseInt(document.getElementById('people').value, 10);
    const reservationTime = document.getElementById('date').value;
    const tableId = document.getElementById('tableId').value;
    const name = document.getElementById('name').value;

    if (!tableId) {
        alert('Пожалуйста, выберите столик.');
        return;
    }

    const requestData = { 
        name: name,
        table: { id: tableId },
        reservationTime: reservationTime,
        numberOfPeople: people 
    };

    try {
        const response = await fetch(`${API_URL}/api/reserve`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(requestData),
        });

        const responseData = await response.json();

        if (!response.ok) {
            alert(responseData.error || 'Ошибка при бронировании столика. Пожалуйста, попробуйте снова.');
            return;
        }

        alert(responseData.message || 'Столик успешно забронирован!');
        // Обновим статус столика после успешного бронирования
        const selectedTable = tableData.find(table => table.id === parseInt(tableId));
        selectedTable.reserved = true;
        selectedTable.element.classList.add('occupied');
    } catch (error) {
        console.error('Ошибка при бронировании:', error);
        alert('Ошибка при бронировании столика: ' + error.message);
    }
});