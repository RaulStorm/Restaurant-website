const API_URL = "http://192.168.1.70:8080"; // Убедитесь, что URL доступен из браузера
let cart = []; // Товары в корзине
let allMenuItems = []; // Все блюда с категорией

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch(`${API_URL}/api/menu`);
        if (!response.ok) {
            throw new Error('Ошибка сети: ' + response.status);
        }
        const data = await response.json();

        const categories = {};

        // Группируем по категориям
        data.forEach(item => {
            const categoryName = item.categoryName || 'Другие';
            if (!categories[categoryName]) {
                categories[categoryName] = [];
            }
            categories[categoryName].push(item);
        });

        createTabButtons(Object.keys(categories));
        saveMenuItems(categories);
        renderItems('all');
    } catch (error) {
        console.error('Ошибка при загрузке меню:', error);
    }
});

// Сохраняем все блюда с указанием категории
function saveMenuItems(categories) {
    allMenuItems = Object.entries(categories).flatMap(([category, items]) =>
        items.map(item => ({ ...item, category }))
    );
}

// Создаём кнопки вкладок
function createTabButtons(categoryNames) {
    const tabsContainer = document.querySelector('.tabs');

    const allButton = document.createElement('button');
    allButton.className = 'tab-button active';
    allButton.textContent = 'Все';
    allButton.dataset.category = 'all';
    allButton.addEventListener('click', () => {
        showItems('all');
        setActiveTab(allButton);
    });
    tabsContainer.appendChild(allButton);

    categoryNames.forEach(name => {
        const tabButton = document.createElement('button');
        tabButton.className = 'tab-button';
        tabButton.textContent = name;
        tabButton.dataset.category = name;

        tabButton.addEventListener('click', () => {
            showItems(name);
            setActiveTab(tabButton);
        });

        tabsContainer.appendChild(tabButton);
    });
}

// Отображение блюд по категории
function showItems(category) {
    renderItems(category);
}

function renderItems(selectedCategory) {
    const menuItemsContainer = document.getElementById('menu-items');
    menuItemsContainer.innerHTML = '';

    const itemsToShow = selectedCategory === 'all'
        ? allMenuItems
        : allMenuItems.filter(item => item.category === selectedCategory);

    itemsToShow.forEach(item => {
        const imageUrl = item.images?.[0] || '/default-image.jpg';  // Ссылка на изображение из Cloudinary

        const menuItemDiv = document.createElement('div');
        menuItemDiv.className = 'menu-item flip-card';

        menuItemDiv.innerHTML = `
            <div class="flip-card-inner">
                <div class="flip-card-front">
                    <img src="${imageUrl}" alt="${item.name}" />
                    <h4>${item.name}</h4>
                    <div class="price">${item.price.toFixed(2)} ₽</div>
                    <p class="description">${item.description || 'Описание отсутствует'}</p>
                </div>
                <div class="flip-card-back">
                    <img src="${imageUrl}" alt="${item.name}" />
                    <h4>${item.name}</h4>
                    <div class="quantity-block">
                        <input type="number" class="item-quantity" min="1" value="1">
                    </div>
                    <button class="add-to-cart" data-id="${item.id}">Добавить в корзину</button>
                </div>
            </div>
        `;

        menuItemsContainer.appendChild(menuItemDiv);
    });

    attachCartListeners();
}

// Подсветка активной вкладки
function setActiveTab(activeButton) {
    const buttons = document.querySelectorAll('.tab-button');
    buttons.forEach(btn => btn.classList.remove('active'));
    activeButton.classList.add('active');
}

// Назначаем обработчики "Добавить в корзину"
function attachCartListeners() {
    const buttons = document.querySelectorAll('.add-to-cart');
    buttons.forEach(button => {
        button.addEventListener('click', handleAddToCart);
    });
}

// Обработка добавления в корзину
function handleAddToCart(event) {
    const itemId = event.target.dataset.id;
    const itemContainer = event.target.closest('.flip-card');
    const itemName = itemContainer.querySelector('h4').textContent;
    const itemPrice = parseFloat(itemContainer.querySelector('.price').textContent.replace(' ₽', '').trim());
    const itemQuantity = parseInt(itemContainer.querySelector('.item-quantity').value);

    if (isNaN(itemQuantity) || itemQuantity <= 0) {
        alert("Введите корректное количество.");
        return;
    }

    const existingItem = cart.find(item => item.id === itemId);

    if (existingItem) {
        existingItem.quantity += itemQuantity;
    } else {
        cart.push({ id: itemId, name: itemName, price: itemPrice, quantity: itemQuantity });
    }

    updateCart();
}
function handleRemoveItem(event) {
    const itemId = event.target.dataset.id;
    cart = cart.filter(item => item.id !== itemId);
    updateCart();
}

function updateCart() {
    const cartItemsContainer = document.querySelector('#cart-items');
    const totalPriceContainer = document.querySelector('#total-price');

    cartItemsContainer.innerHTML = '';

    let totalPrice = 0;

    cart.forEach(item => {
        const itemRow = document.createElement('div');
        itemRow.className = 'cart-item';

        itemRow.innerHTML = `
            ${item.name} x ${item.quantity} — ${(item.price * item.quantity).toFixed(2)} ₽
            <button class="remove-item" data-id="${item.id}">−</button>
        `;

        cartItemsContainer.appendChild(itemRow);
        totalPrice += item.price * item.quantity;
    });

    totalPriceContainer.textContent = totalPrice.toFixed(2);

    // Назначаем обработчики на кнопки "−"
    const removeButtons = document.querySelectorAll('.remove-item');
    removeButtons.forEach(button => {
        button.addEventListener('click', handleRemoveItem);
    });

    toggleClearCartIcon(cart.length > 0);
}


// Отправка заказа
document.getElementById('place-order').addEventListener('click', async () => {
    if (cart.length === 0) {
        alert("Корзина пуста.");
        return;
    }

    const tableNumber = document.getElementById('table-number').value.trim();
    const orderNotes = document.getElementById('order-notes').value.trim();

    if (!tableNumber) {
        alert("Введите номер столика.");
        return;
    }

    const orderData = {
        tableNumber,
        orderNotes,
        items: cart.map(item => ({
            menuItemId: item.id,
            quantity: item.quantity
        }))
    };

    try {
        const token = localStorage.getItem('token');
        if (!token) {
            alert("Вы не авторизованы.");
            return;
        }

        const response = await fetch(`${API_URL}/api/orders`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error("Ошибка сервера: " + errText);
        }

        const result = await response.json();
        alert("Заказ оформлен!");

        // Очистка
        cart = [];
        updateCart();
        document.getElementById('table-number').value = '';
        document.getElementById('order-notes').value = '';
    } catch (error) {
        console.error("Ошибка оформления:", error);
        alert("Ошибка оформления заказа.");
    }
});

document.getElementById('clear-cart').addEventListener('click', () => {
    cart = [];
    updateCart();
    document.getElementById('table-number').value = '';
    document.getElementById('order-notes').value = '';
});

function toggleClearCartIcon(show) {
    const clearButton = document.getElementById('clear-cart');
    clearButton.style.display = show ? 'block' : 'none';
}

