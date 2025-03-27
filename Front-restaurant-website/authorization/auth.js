const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный и доступен

document.addEventListener('DOMContentLoaded', function() {
    const sign_in_btn = document.querySelector("#sign-in-btn");
    const sign_up_btn = document.querySelector("#sign-up-btn");
    const container = document.querySelector(".container");

    // Переключение между режимами входа и регистрации
    sign_up_btn.addEventListener('click', () => {
        container.classList.add("sign-up-mode");
    });

    sign_in_btn.addEventListener('click', () => {
        container.classList.remove("sign-up-mode");
    });

    // Обработчик отправки формы входа
    const signInForm = document.querySelector('.sign-in-form');
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

        // Отправка данных для входа на сервер
        fetch(`${API_URL}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password }) // Отправка email и password
        })
        .then(response => response.ok ? response.json() : Promise.reject(response.json()))
        .then(data => {
            if (data.success) {
                console.log('Вход выполнен');
                // Сохраните полученный токен в localStorage или sessionStorage
                localStorage.setItem('token', data.token);
                // Переход на главную страницу
                window.location.href = '/';
            } else {
                console.error('Ошибка входа:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => error.then(err => console.error('Ошибка:', err)));
    });

    // Обработчик отправки формы регистрации
    const signUpForm = document.querySelector('.sign-up-form');
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

        // Отправка данных для регистрации на сервер
        fetch(`${API_URL}/api/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email, password })
        })
        .then(response => response.ok ? response.json() : Promise.reject(response.json()))
        .then(data => {
            if (data.success) {
                console.log('Регистрация выполнена');
                // Перенаправление на страницу входа
                window.location.href = '/login.html';
            } else {
                console.error('Ошибка регистрации:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => error.then(err => console.error('Ошибка:', err)));
    });
});
