
function logout() {

    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');

    fetch('/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'same-origin'
    })
    .then(response => {
        if (response.ok) {
            window.location.href = '/login';
        } else {
            console.error('로그아웃 실패');
        }
    })
    .catch(error => {
        console.error('로그아웃 중 에러 발생', error);
    });
}
