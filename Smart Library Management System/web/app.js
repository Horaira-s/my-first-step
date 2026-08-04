async function loadData() {
  const booksResponse = await fetch('/api/books');
  const books = await booksResponse.json();
  const membersResponse = await fetch('/api/members');
  const members = await membersResponse.json();

  renderBooks(books);
  renderMembers(members);
}

function renderBooks(books) {
  const container = document.getElementById('books-list');
  if (!books.length) {
    container.innerHTML = '<div class="card">No books yet.</div>';
    return;
  }

  container.innerHTML = books.map(book => `
    <div class="card">
      <strong class="book-title">${book.title}</strong><br />
      ID: ${book.bookId}<br />
      Author: ${book.author}<br />
      Category: ${book.category}<br />
      <span class="badge">${book.available ? 'Available' : 'Issued'}</span>
    </div>
  `).join('');
}

function renderMembers(members) {
  const container = document.getElementById('members-list');
  if (!members.length) {
    container.innerHTML = '<div class="card">No members yet.</div>';
    return;
  }

  container.innerHTML = members.map(member => `
    <div class="card">
      <strong class="member-name">${member.name}</strong><br />
      ID: ${member.id}<br />
      Email: ${member.email}<br />
      Borrowed books: ${member.borrowedBooks.length ? member.borrowedBooks.join(', ') : 'None'}
    </div>
  `).join('');
}

document.getElementById('book-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  const response = await fetch('/api/books', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(formData).toString()
  });
  const result = await response.json();
  document.getElementById('book-message').textContent = result.status === 'ok' ? 'Book added!' : result.message;
  loadData();
});

document.getElementById('member-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  const response = await fetch('/api/members', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(formData).toString()
  });
  const result = await response.json();
  document.getElementById('member-message').textContent = result.status === 'ok' ? 'Member added!' : result.message;
  loadData();
});

document.getElementById('issue-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.target);
  const action = event.submitter && event.submitter.dataset.action ? event.submitter.dataset.action : 'issue';
  const response = await fetch(action === 'return' ? '/api/return' : '/api/issue', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(formData).toString()
  });
  const result = await response.json();
  document.getElementById('issue-message').textContent = result.status === 'ok' ? (action === 'return' ? 'Book returned!' : 'Book issued!') : result.message;
  loadData();
});

loadData();
