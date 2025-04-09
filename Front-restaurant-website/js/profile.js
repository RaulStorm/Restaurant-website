// profile.js

const API_URL = "http://localhost:8080";

// Функция декодирования JWT
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
  // Получаем токен из localStorage
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "/authorization/auth.html";
    return;
  }
  
  // Декодируем токен и устанавливаем данные пользователя
  const payload = parseJwt(token);
  if (payload) {
    document.getElementById("user-name").textContent = payload.name || "Имя пользователя";
    document.getElementById("user-email").textContent = payload.sub || "email@example.com";
  } else {
    console.error("Не удалось декодировать токен");
  }
  
  // Загрузка истории заказов
  fetch(`${API_URL}/api/profile/orders`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const list = document.getElementById("order-history");
    list.innerHTML = "";
    if (Array.isArray(data)) {
      data.forEach(order => {
        const li = document.createElement("li");
        li.textContent = `Заказ #${order.id} - ${order.status}`;
        list.appendChild(li);
      });
    } else {
      console.error("Получены данные заказов, но они не являются массивом:", data);
    }
  })
  .catch(err => console.error("Ошибка загрузки заказов:", err));
  
  // Загрузка бронирований столика
  fetch(`${API_URL}/api/profile/reservations`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const reservationInfo = document.getElementById("reservation-info");
    if (Array.isArray(data) && data.length > 0) {
      const reservation = data[0]; // Берем первое бронирование (можно изменить логику)
      reservationInfo.textContent = `Дата: ${reservation.date}, Время: ${reservation.time}, Столик: ${reservation.tableNumber}`;
    } else {
      reservationInfo.textContent = "Вы не бронировали столик.";
    }
  })
  .catch(err => console.error("Ошибка загрузки бронирования:", err));
  
  // Загрузка списка любимых блюд
  fetch(`${API_URL}/api/profile/favorite-dishes`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const favList = document.getElementById("favorite-dishes");
    favList.innerHTML = "";
    if (Array.isArray(data)) {
      data.forEach(dish => {
        const li = document.createElement("li");
        li.textContent = dish.name;
        favList.appendChild(li);
      });
    } else {
      console.error("Получены данные любимых блюд, но они не являются массивом:", data);
    }
  })
  .catch(err => console.error("Ошибка загрузки любимых блюд:", err));
  
  // Загрузка отзыва пользователя
  fetch(`${API_URL}/api/profile/user-review`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const reviewElement = document.getElementById("user-review");
    if (data && data.reviewText) {
      reviewElement.textContent = data.reviewText;
    } else {
      reviewElement.textContent = "Отзыва пока нет.";
      document.getElementById("add-review-btn").style.display = "block";
    }
  })
  .catch(err => console.error("Ошибка загрузки отзыва:", err));
  
  // Обработка кнопки выхода
  document.getElementById("logout-btn").addEventListener("click", () => {
    localStorage.removeItem("token");
    window.location.href = "/index.html";
  });
});
