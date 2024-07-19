document.addEventListener('DOMContentLoaded', () => {
    const imageUploadForm = document.getElementById('image-upload-form');

    // 이미지 업로드 폼 제출 시
    if (imageUploadForm) {
        imageUploadForm.addEventListener('submit', function(event) {
            event.preventDefault(); // 기본 폼 제출 방지

            const formData = new FormData(imageUploadForm);


            const articleId = document.getElementById('articleId').value;
            formData.append('articleId', articleId);

            function success() {
                alert('이미지 업로드가 완료되었습니다.');
                location.replace('/articles/' + articleId);
            }

            function fail() {
                alert('이미지 업로드에 실패했습니다.');
                location.replace('/articles/' + articleId);
            }

            imageHttpRequest('POST', imageUploadForm.action, formData, success, fail);
        });
    }
});

// 쿠키를 가져오는 함수
function getCookie(key) {
    let result = null;
    let cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        let dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

function imageHttpRequest(method, url, body, success, fail) {
    const token = localStorage.getItem('access_token'); // 로컬 스토리지에서 액세스 토큰 추출

    fetch(url, {
        method: method,
        headers: token ? {
            'Authorization': 'Bearer ' + token
            // 'Content-Type': 'application/json'  // FormData를 사용할 때는 이 헤더를 설정할 필요 없음
        } : {},
        body: body
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refreshToken = getCookie('refresh_token');
        if (response.status === 401 && refreshToken) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: refreshToken,
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    } else {
                        throw new Error('Refresh token request failed');
                    }
                })
                .then(result => { // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem('access_token', result.accessToken);
                    // 재귀 호출로 원래 요청을 다시 시도
                    imageHttpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    }).catch(error => fail());
}

// 미리보기 기능
document.getElementById('file-upload').addEventListener('change', function(event) {
    const files = event.target.files;
    const previewContainer = document.querySelector('.preview-container');
    previewContainer.innerHTML = ''; // 미리보기 초기화

    Array.from(files).forEach(file => {
        const reader = new FileReader();

        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            previewContainer.appendChild(img);
        };

        reader.readAsDataURL(file);
    });
});
