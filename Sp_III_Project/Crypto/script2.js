
const itemsPerPage = 10;
let currentPage = 1;
let cryptocurrencies = [];

// Fetch Crypto Data
async function fetchCryptoData() {
  try {
    const response = await fetch('https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=100&page=1&sparkline=false');
    cryptocurrencies = await response.json();
    updateTable();
    updatePagination();
  } catch (error) {
    console.error('Error fetching data:', error);
  }
}

// Update Table
function updateTable() {
  const tableBody = document.getElementById('crypto-data');
  tableBody.innerHTML = '';

  const startIdx = (currentPage - 1) * itemsPerPage;
  const endIdx = startIdx + itemsPerPage;
  const itemsToDisplay = cryptocurrencies.slice(startIdx, endIdx);

  itemsToDisplay.forEach(crypto => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td><img src="${crypto.image}" alt="${crypto.name}" class="crypto-logo" /></td>
      <td>${crypto.name}</td>
      <td>${crypto.symbol.toUpperCase()}</td>
      <td>$${crypto.current_price.toLocaleString()}</td>
      <td style="color: ${crypto.price_change_percentage_24h < 0 ? 'red' : 'green'};">
        ${crypto.price_change_percentage_24h.toFixed(2)}%
      </td>
    `;
    tableBody.appendChild(row);
  });
}

// Update Pagination
function updatePagination() {
  const paginationNav = document.getElementById('pagination');
  paginationNav.innerHTML = '';

  const totalPages = Math.ceil(cryptocurrencies.length / itemsPerPage);

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement('button');
    btn.textContent = i;
    if (i === currentPage) btn.classList.add('active');
    btn.addEventListener('click', () => {
      currentPage = i;
      updateTable();
      updatePagination();
    });
    paginationNav.appendChild(btn);
  }
}

// Initial Load
fetchCryptoData();
