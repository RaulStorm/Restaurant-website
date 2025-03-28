const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен

document.addEventListener('DOMContentLoaded', function() {
    const userAuth = document.getElementById('user-auth'); // Элемент <li>
    const userIcon = document.getElementById('user-icon'); // Иконка входа
    const token = localStorage.getItem('token'); // Проверяем токен

    if (token) {
        // Если пользователь авторизован, меняем иконку и ссылку
        userIcon.src = "/authorization/img/profile.png"; // Путь к иконке профиля
        userAuth.querySelector('a').href = "/profile.html"; // Меняем ссылку на профиль
    }

    const sign_in_btn = document.querySelector("#sign-in-btn");
    const sign_up_btn = document.querySelector("#sign-up-btn");
    const container = document.querySelector(".container");

    if (sign_up_btn && sign_in_btn && container) {
        sign_up_btn.addEventListener('click', () => {
            container.classList.add("sign-up-mode");
        });

        sign_in_btn.addEventListener('click', () => {
            container.classList.remove("sign-up-mode");
        });
    }

    // Обработчик формы входа
    const signInForm = document.querySelector('.sign-in-form');
    if (signInForm) {
        signInForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const emailInput = signInForm.querySelector('input[name="email"]');
            const passwordInput = signInForm.querySelector('input[name="password"]');

            if (!emailInput || !passwordInput) {
                console.error('Не удалось получить значения формы');
                return;
            }

            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            fetch(`${API_URL}/api/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            })
            .then(response => response.ok ? response.json() : Promise.reject(response.json()))
            .then(data => {
                if (data.success) {
                    console.log('Вход выполнен');
                    localStorage.setItem('token', data.token);
                    window.location.href = '/страницы/profile.html'; // Перенаправление на страницу профиля
                } else {
                    console.error('Ошибка входа:', data.message || 'Неизвестная ошибка');
                }
            })
            .catch(error => error.then(err => console.error('Ошибка:', err)));
        });
    }

    // Обработчик формы регистрации
    const signUpForm = document.querySelector('.sign-up-form');
    if (signUpForm) {
        signUpForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const nameInput = signUpForm.querySelector('input[name="name"]');
            const emailInput = signUpForm.querySelector('input[name="email"]');
            const passwordInput = signUpForm.querySelector('input[name="password"]');

            if (!nameInput || !emailInput || !passwordInput) {
                console.error('Не удалось получить значения формы');
                return;
            }

            const name = nameInput.value.trim();
            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            fetch(`${API_URL}/api/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password })
            })
            .then(response => response.ok ? response.json() : Promise.reject(response.json()))
            .then(data => {
                if (data.success) {
                    console.log('Регистрация выполнена');
                    window.location.href = '/index.html'; // Перенаправление на главную страницу
                } else {
                    console.error('Ошибка регистрации:', data.message || 'Неизвестная ошибка');
                }
            })
            .catch(error => error.then(err => console.error('Ошибка:', err)));
        });
    }
});
