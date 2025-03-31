const API_URL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('reservation-form').addEventListener('submit', async function(event) {
        event.preventDefault();

        // Получаем токен из localStorage
        const token = localStorage.getItem('token');

        if (!token) {
            alert('Ошибка: Пользователь не авторизован.');
            return;
        }

        // Получаем значения из формы
        const people = parseInt(document.getElementById('people').value, 10);
        const reservationTime = document.getElementById('date').value;
        const tableId = document.getElementById('tableId').value;

        // Формируем данные
        const requestData = { 
            table: { id: tableId }, 
            reservationTime: reservationTime, 
            numberOfPeople: people 
        };

        console.log("Отправляемые данные:", requestData);

        try {
            const response = await fetch(`${API_URL}/api/reserve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`  // 🟢 Передаем токен
                },
                body: JSON.stringify(requestData),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Ошибка запроса: ${response.status} - ${errorText}`);
            }

            alert('Столик успешно забронирован!');
        } catch (error) {
            console.error('Ошибка бронирования:', error);
            alert('Ошибка при бронировании столика.');
        }
    });
});

function parseJwt(token) {
const base64Url = token.split('.')[1]; // payload
const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

const jsonPayload = decodeURIComponent(
atob(base64)
    .split('')
    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
    .join('')
);


let storedToken  = localStorage.getItem("token");
}
