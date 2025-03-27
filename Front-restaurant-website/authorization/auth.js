const API_URL = "http://localhost:8080"; // Убедитесь, что этот URL правильный

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    const logoutButton = document.getElementById("logout-button");
    const userIcon = document.getElementById("user-icon");
    const userInfo = document.getElementById("user-info"); // Если нужно отображать имя пользователя

    function getToken() {
        return localStorage.getItem("token");
    }

    function saveToken(token) {
        localStorage.setItem("token", token);
    }

    function removeToken() {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    }

    async function updateUserUI() {
        const token = getToken();

        if (!token) {
            console.log("Пользователь не авторизован. Устанавливаем роль Guest.");
            userInfo.textContent = "Гость"; // Или любая другая надпись для неавторизованного пользователя
            userIcon.src = "/authorization/img/auth.png";
            userIcon.alt = "Вход";
            userIcon.closest("a").href = "/authorization/auth.html"; // Устанавливаем ссылку на страницу авторизации
            return;
        }

        try {
            const response = await fetch(API_URL + "/api/user-info", {
                method: "GET",
                headers: { "Authorization": "Bearer " + token }
            });

            if (!response.ok) {
                throw new Error("Ошибка при получении данных пользователя");
            }

            const userData = await response.json();
            console.log("Данные пользователя:", userData);

            if (userData.success) {
                localStorage.setItem("user", JSON.stringify(userData)); // Сохраняем пользователя в localStorage
                userInfo.textContent = userData.name || userData.email; // Отображаем имя или email пользователя
                userIcon.src = "/authorization/img/avatar-icon.png";
                userIcon.alt = "Личный кабинет";
                userIcon.closest("a").href = "/profile.html"; // Ссылка на личный кабинет
            } else {
                throw new Error("Ошибка аутентификации");
            }
        } catch (error) {
            console.error("Ошибка при получении данных пользователя:", error);
            removeToken();
            updateUserUI();
        }
    }

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const email = document.getElementById("login-email").value;
            const password = document.getElementById("login-password").value;

            try {
                const response = await fetch(API_URL + "/api/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();
                if (data.success) {
                    saveToken(data.token);
                    await updateUserUI();
                    window.location.href = "/"; // Перенаправляем на главную
                } else {
                    alert("Ошибка входа: " + data.message);
                }
            } catch (error) {
                console.error("Ошибка при входе:", error);
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const name = document.getElementById("register-name").value;
            const email = document.getElementById("register-email").value;
            const password = document.getElementById("register-password").value;

            try {
                const response = await fetch(API_URL + "/api/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name, email, password })
                });

                const data = await response.json();
                if (data.success) {
                    alert("Регистрация успешна! Теперь войдите.");
                    window.location.href = "/authorization/auth.html";
                } else {
                    alert("Ошибка регистрации: " + data.message);
                }
            } catch (error) {
                console.error("Ошибка регистрации:", error);
            }
        });
    }

    if (logoutButton) {
        logoutButton.addEventListener("click", () => {
            removeToken();
            updateUserUI();
            window.location.href = "/";
        });
    }

    updateUserUI();
});
