// profile.js

const API_URL = "http://192.168.1.70:8080"; // Убедитесь, что URL доступен из браузера

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
  
  // Загрузка бронирования столика
  fetch(`${API_URL}/api/profile/reservations`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const reservationInfo = document.getElementById("reservation-info");
    reservationInfo.innerHTML = "";
    
    if (Array.isArray(data) && data.length > 0) {
      data.forEach(reservation => {
        const li = document.createElement("li");
  
        // Формируем строку с полной информацией о бронировании
        li.innerHTML = `
          <strong>Дата: ${reservation.reservationTime}</strong><br>
          Количество людей: ${reservation.numberOfPeople}<br>
          Столик: ${reservation.table.tableNumber}<br>
          Имя для бронирования: ${reservation.name}
        `;
        
        // Добавляем кнопку для отмены брони
        const cancelButton = document.createElement("button");
        cancelButton.textContent = "Отменить бронь";
        cancelButton.classList.add("cancel-btn");
  
        // Убедитесь, что reservation.id передается правильно
        cancelButton.onclick = () => cancelReservation(reservation.id);  // Передаем правильный ID
  
        li.appendChild(cancelButton);
  
        reservationInfo.appendChild(li);
      });
    } else {
      reservationInfo.textContent = "У вас нет будущих бронирований.";
    }
  })
  .catch(err => console.error("Ошибка загрузки бронирований:", err));
  
  function cancelReservation(reservationId) {
    const token = localStorage.getItem("token");
  
    fetch(`${API_URL}/api/profile/reservations/${reservationId}`, {
      method: 'DELETE',
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
    .then(res => {
      if (!res.ok) {
        return res.text().then(text => { throw new Error(text); });
      }
      return res.text();
    })
    .then(data => {
      alert(data);  // сообщение от сервера
      location.reload();  // перезагружаем для обновления
    })
    .catch(err => {
      console.error("Ошибка отмены брони:", err);
      alert("Ошибка отмены бронирования: " + err.message);
    });
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
        li.innerHTML = `<strong>Заказ #${order.id}</strong>`;
  
        const ul = document.createElement("ul");
        order.items.forEach(item => {
          const itemLi = document.createElement("li");
          itemLi.textContent = `${item.name} x${item.quantity}`;
          ul.appendChild(itemLi);
        });
  
        li.appendChild(ul);
        list.appendChild(li);
      });
    } else {
      console.error("Некорректный формат данных заказов:", data);
    }
  })
  .catch(err => console.error("Ошибка загрузки заказов:", err));
  
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
        li.innerHTML = `
          <strong>${dish.name}</strong><br>
          ${dish.description || 'Нет описания'}<br>
          Цена: ${dish.price}₽
        `;
        // Отображаем изображение, если оно есть
        if (dish.images && dish.images.length > 0) {
          li.innerHTML += `<img src="${dish.images[0]}" alt="${dish.name}" />`;
        }
        favList.appendChild(li);
      });
    } else {
      console.error("Получены данные любимых блюд, но они не являются массивом:", data);
    }
  })
  .catch(err => console.error("Ошибка загрузки любимых блюд:", err));
  

  fetch(`${API_URL}/api/profile/user-review`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const reviewElement = document.getElementById("user-review");
    const button = document.getElementById("review-btn");
  
    if (data && data.reviewText) {
      reviewElement.innerHTML = `
        <strong>Дата:</strong> ${data.formattedDate}<br>
        <strong>Оценка:</strong> ${data.rating} ⭐<br>
        <strong>Отзыв:</strong><br>${data.reviewText}
      `;
      button.textContent = "Оставить новый отзыв";
    } else {
      reviewElement.textContent = "Отзыва пока нет.";
      button.textContent = "Оставить отзыв";
    }
  
    button.style.display = "inline-block";
    button.onclick = () => {
      window.location.href = "/страницы/review.html";
    };
  })
  .catch(err => console.error("Ошибка загрузки отзыва:", err));
  
  document.getElementById("logout-btn").addEventListener("click", () => {
    // Удаление токена и имени пользователя
    localStorage.removeItem("token");
    localStorage.removeItem("name");  // Удаляем имя пользователя

    // Перенаправление на главную страницу или страницу авторизации
    window.location.href = "/index.html"; // Перенаправление на главную страницу
  });
});


