const API_URL = "http://192.168.1.70:8080"; // Убедитесь, что этот URL правильный и доступен

document.addEventListener("DOMContentLoaded", () => {
    const signInForm = document.querySelector('.sign-in-form'); // Убедитесь, что это правильный селектор
    if (!signInForm) {
        console.error("Не найден элемент формы входа");
        return;
    }

    // Обработчик отправки формы входа
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
                localStorage.setItem('token', data.token); // Сохраняем токен
                window.location.href = '/';
            } else {
                console.error('Ошибка входа:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => console.log(error));
    });

    // Обработчик отправки формы регистрации
    const signUpForm = document.querySelector('.sign-up-form');
    if (!signUpForm) {
        console.error("Не найден элемент формы регистрации");
        return;
    }

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
            if (data.success && data.token) {
                console.log('Регистрация выполнена');
        
                // ✅ Сохраняем токен нового пользователя
                localStorage.setItem('token', data.token);
        
                // ✅ Можно сохранить имя, если нужно
                localStorage.setItem('name', data.name);
        
                // ✅ Перенаправляем сразу на главную страницу
                window.location.href = '/';
            } else {
                console.error('Ошибка регистрации:', data.message || 'Неизвестная ошибка');
            }
        })
        .catch(error => error.then(err => console.error('Ошибка:', err)));
    });

    // Логика переключения между формами входа и регистрации
    const signUpBtn = document.querySelector("#sign-up-btn");
    const signInBtn = document.querySelector("#sign-in-btn");
    const container = document.querySelector(".container");

    // Переключение между режимами входа и регистрации
    signUpBtn.addEventListener('click', () => {
        container.classList.add("sign-up-mode");
    });

    signInBtn.addEventListener('click', () => {
        container.classList.remove("sign-up-mode");
    });

    // Логика социальных кнопок
    const socialIcons = document.querySelectorAll(".social-icon");
    socialIcons.forEach(icon => {
        icon.addEventListener('click', (e) => {
            e.preventDefault();
            console.log(`Выбран метод: ${e.target.innerText}`);
        });
    });
});
