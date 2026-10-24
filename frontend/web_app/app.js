// Online Auction System - Client Web App
document.addEventListener('DOMContentLoaded', () => {
  const auctions = [
    {
      id: 1,
      title: "Xe Ô tô Cổ Mercedes-Benz 280SL Pagoda 1969",
      type: "Động sản",
      method: "Tăng dần (Trả giá lên)",
      status: "live",
      statusText: "Đang diễn ra",
      startPrice: 2500000000,
      currentPrice: 2850000000,
      stepPrice: 50000000,
      endTime: new Date(Date.now() + 1000 * 60 * 45), // 45 mins left
      image: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80",
      bidsCount: 18
    },
    {
      id: 2,
      title: "Biệt thự Đơn lập Ven Sông KĐT Thảo Điền (520m²)",
      type: "Bất động sản",
      method: "Tăng dần (Trả giá lên)",
      status: "live",
      statusText: "Đang diễn ra",
      startPrice: 85000000000,
      currentPrice: 91500000000,
      stepPrice: 500000000,
      endTime: new Date(Date.now() + 1000 * 60 * 120), // 2 hours left
      image: "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
      bidsCount: 12
    },
    {
      id: 3,
      title: "Lô 50 Tấn Cà Phê Robusta Tiêu Chuẩn Xuất Khẩu",
      type: "Động sản",
      method: "Giảm dần (Đặt giá xuống)",
      status: "live",
      statusText: "Đang diễn ra",
      startPrice: 5000000000,
      currentPrice: 4750000000,
      floorPrice: 4200000000,
      stepPrice: 50000000,
      endTime: new Date(Date.now() + 1000 * 60 * 30),
      image: "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=800&q=80",
      bidsCount: 0
    },
    {
      id: 4,
      title: "Đồng Hồ Patek Philippe Grand Complications 5270P",
      type: "Động sản",
      method: "Tăng dần (Trả giá lên)",
      status: "upcoming",
      statusText: "Chờ mở phiên",
      startPrice: 4800000000,
      currentPrice: 4800000000,
      stepPrice: 100000000,
      endTime: new Date(Date.now() + 1000 * 60 * 60 * 24),
      image: "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=800&q=80",
      bidsCount: 0
    }
  ];

  const grid = document.getElementById('auctionGrid');
  const searchInput = document.getElementById('searchInput');
  const typeFilter = document.getElementById('typeFilter');
  const methodFilter = document.getElementById('methodFilter');

  function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
  }

  function renderAuctions(list) {
    if (!grid) return;
    grid.innerHTML = '';
    
    if (list.length === 0) {
      grid.innerHTML = `<div style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 3rem;">
        Không tìm thấy phiên đấu giá nào phù hợp với bộ lọc.
      </div>`;
      return;
    }

    list.forEach(item => {
      const card = document.createElement('div');
      card.className = 'auction-card';
      card.innerHTML = `
        <div class="card-media">
          <img src="${item.image}" alt="${item.title}" loading="lazy" />
          <span class="status-badge status-live">${item.statusText}</span>
          <span class="method-tag">${item.method}</span>
        </div>
        <div class="card-body">
          <h3 class="card-title">${item.title}</h3>
          <div class="card-meta">
            <span>🏷️ ${item.type}</span>
            <span>🔥 ${item.bidsCount} lượt trả giá</span>
          </div>
          <div class="price-box">
            <div>
              <div class="price-label">Giá hiện tại</div>
              <div class="price-value">${formatVND(item.currentPrice)}</div>
            </div>
            <div style="text-align: right;">
              <div class="price-label">Bước giá</div>
              <div style="font-weight: 600; font-size: 0.95rem; color: #fff;">+${formatVND(item.stepPrice)}</div>
            </div>
          </div>
          <div class="card-footer">
            <a href="auction-room.html?id=${item.id}" class="btn btn-gold">
              ⚡ Vào Phòng Đấu Giá
            </a>
          </div>
        </div>
      `;
      grid.appendChild(card);
    });
  }

  function filterData() {
    const query = searchInput ? searchInput.value.toLowerCase().trim() : '';
    const type = typeFilter ? typeFilter.value : '';
    const method = methodFilter ? methodFilter.value : '';

    const filtered = auctions.filter(item => {
      const matchQuery = item.title.toLowerCase().includes(query);
      const matchType = !type || item.type === type;
      const matchMethod = !method || item.method.includes(method);
      return matchQuery && matchType && matchMethod;
    });

    renderAuctions(filtered);
  }

  if (searchInput) searchInput.addEventListener('input', filterData);
  if (typeFilter) typeFilter.addEventListener('change', filterData);
  if (methodFilter) methodFilter.addEventListener('change', filterData);

  // Initial render
  renderAuctions(auctions);
});
