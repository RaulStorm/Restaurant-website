document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/menu')
        .then(response => response.json())
        .then(data => {
            const menuItemsContainer = document.getElementById('menu-items');
            data.forEach(item => {
                const div = document.createElement('div');
                div.innerHTML = `<h3>${item.name}</h3><p>${item.description}</p><p>Цена: ${item.price}₽</p>`;
                menuItemsContainer.appendChild(div);
            });
        });
});
