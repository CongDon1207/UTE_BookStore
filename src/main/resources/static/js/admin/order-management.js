let currentModal = null;

function showToast(message, type = 'success') {
    const toast = new bootstrap.Toast(document.querySelector('.toast'));
    const toastBody = document.querySelector('.toast-body');
    toastBody.textContent = message;
    toastBody.className = 'toast-body text-' + type;
    toast.show();
}

function viewOrderDetail(orderId) {
    // Hiển thị loading state
    const modalContent = document.getElementById('orderDetailContent');
    modalContent.innerHTML = '<div class="text-center p-4"><div class="spinner-border" role="status"></div><p class="mt-2">Đang tải...</p></div>';
    
    // Mở modal trước khi fetch data
    if (!currentModal) {
        currentModal = new bootstrap.Modal(document.getElementById('orderDetailModal'));
    }
    currentModal.show();
    
    // Fetch data
    fetch(`/admin/orders/${orderId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.text();
        })
        .then(html => {
            if (html.trim()) {
                modalContent.innerHTML = html;
            } else {
                throw new Error('Không tìm thấy thông tin đơn hàng');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            modalContent.innerHTML = `
                <div class="alert alert-danger m-3">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    ${error.message || 'Có lỗi xảy ra khi tải thông tin đơn hàng'}
                </div>`;
            showToast('Có lỗi xảy ra khi tải thông tin đơn hàng', 'danger');
        });
}

function cancelOrder(orderId) {
    if (!confirm('Bạn có chắc muốn hủy đơn hàng này?')) {
        return;
    }

    fetch(`/admin/orders/${orderId}/cancel`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Network response was not ok');
                });
            }
            return response.json();
        })
        .then(data => {
            showToast(data.message || 'Đã hủy đơn hàng thành công');
            setTimeout(() => window.location.reload(), 1000);
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Có lỗi xảy ra khi hủy đơn hàng: ' + error.message, 'danger');
        });
}

function updateOrderStatus(orderId, status) {
    fetch(`/admin/orders/${orderId}/update-status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: status })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Network response was not ok');
                });
            }
            return response.json();
        })
        .then(data => {
            showToast(data.message || 'Đã cập nhật trạng thái đơn hàng');
            setTimeout(() => window.location.reload(), 1000);
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Có lỗi xảy ra khi cập nhật trạng thái: ' + error.message, 'danger');
            // Khôi phục trạng thái cũ của select box nếu cần
        });
}