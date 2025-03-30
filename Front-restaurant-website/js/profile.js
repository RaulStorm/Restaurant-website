function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const binaryString = atob(base64);
        const utf8Decoder = new TextDecoder("utf-8");
        const decodedText = utf8Decoder.decode(new Uint8Array([...binaryString].map(c => c.charCodeAt(0))));
        return JSON.parse(decodedText);
    } catch (e) {
        console.error("Ошибка декодирования JWT:", e);
        return null;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    let token = localStorage.getItem("token");

    if (!token) {
        window.location.href = "/authorization/auth.html";
        return;
    }

    let payload = parseJwt(token);
    if (payload) {
        document.getElementById("user-name").textContent = payload.name || "Имя пользователя";
        document.getElementById("user-email").textContent = payload.sub || "email@example.com";
    }

    // Загрузка истории заказов
    fetch("/api/orders", {
        headers: { "Authorization": `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(data => {
        let list = document.getElementById("order-history");
        list.innerHTML = "";
        data.forEach(order => {
            let li = document.createElement("li");
            li.textContent = `Заказ #${order.id} - ${order.status}`;
            list.appendChild(li);
        });
    })
    .catch(err => console.error("Ошибка загрузки заказов:", err));

    // Загрузка бронирования столика
    fetch("/api/reservations", {
        headers: { "Authorization": `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(data => {
        let reservationInfo = document.getElementById("reservation-info");
        if (data.length > 0) {
            let reservation = data[0]; // Берем последнее бронирование
            reservationInfo.textContent = `Дата: ${reservation.date}, Время: ${reservation.time}, Столик: ${reservation.tableNumber}`;
        } else {
            reservationInfo.textContent = "Вы не бронировали столик.";
        }
    })
    .catch(err => console.error("Ошибка загрузки бронирования:", err));

    // Кнопка выхода
    document.getElementById("logout-btn").addEventListener("click", () => {
        localStorage.removeItem("token");
        window.location.href = "/index.html";
    });
});
