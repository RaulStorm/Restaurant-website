const API_URL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', function () {
    const signInForm = document.querySelector('.sign-in-form');
    const signUpForm = document.querySelector('.sign-up-form');

    // Обработчик формы входа
    if (signInForm) {
        signInForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            const email = signInForm.querySelector('input[name="email"]').value.trim();
            const password = signInForm.querySelector('input[name="password"]').value.trim();

            // Хешируем пароль перед отправкой (SHA-256)
            const hashedPassword = CryptoJS.SHA256(password).toString();

            try {
                const response = await fetch(`${API_URL}/api/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password: hashedPassword })
                });

                const data = await response.json();
                if (!response.ok) throw new Error(data.message || "Ошибка входа");

                localStorage.setItem('token', data.token);
                window.location.href = '/profile.html'; // Перенаправление на профиль
            } catch (error) {
                console.error('Ошибка входа:', error.message || 'Неизвестная ошибка');
            }
        });
    }

    // Обработчик формы регистрации
    if (signUpForm) {
        signUpForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            const name = signUpForm.querySelector('input[name="name"]').value.trim();
            const email = signUpForm.querySelector('input[name="email"]').value.trim();
            const password = signUpForm.querySelector('input[name="password"]').value.trim();

            // Хешируем пароль перед отправкой
            const hashedPassword = CryptoJS.SHA256(password).toString();

            try {
                const response = await fetch(`${API_URL}/api/register`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, email, password: hashedPassword })
                });

                const data = await response.json();
                if (!response.ok) throw new Error(data.message || "Ошибка регистрации");

                console.log('Регистрация выполнена');
                window.location.href = '/index.html'; // Перенаправление на главную страницу
            } catch (error) {
                console.error('Ошибка регистрации:', error.message || 'Неизвестная ошибка');
            }
        });
    }
});

async function logout() {
    const token = localStorage.getItem('token');

    if (!token) {
        console.warn("Пользователь уже разлогинен");
        window.location.href = '/login.html';
        return;
    }

    try {
        await fetch(`${API_URL}/api/logout`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
    } catch (error) {
        console.error("Ошибка при выходе:", error);
    }

    localStorage.removeItem('token');
    localStorage.removeItem('user');
    console.log('Выход выполнен');
    window.location.href = '/login.html';
}