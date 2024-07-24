

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


function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => { // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}




const commentCreatedButton = document.getElementById('comment-created-btn');



if (commentCreatedButton) {
    commentCreatedButton.addEventListener('click', event => {
        let articleId = document.getElementById('article-id').value;

        let body = JSON.stringify({
            articleId: articleId,
            content: document.getElementById('content').value
        });

        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/articles/' + articleId);
        };

        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/articles/' + articleId);
        };

        httpRequest('POST', '/api/comments', body, success, fail);
    });
}


const commentDeleteButtons = document.querySelectorAll('.comment-delete-btn');

commentDeleteButtons.forEach(button => {
    button.addEventListener('click', event => {
        const commentId = button.getAttribute('data-comment-id');
        const articleId = document.getElementById('article-id').value;

        if (confirm('댓글을 삭제하시겠습니까?')) {

            function success() {
                 alert('삭제가 완료되었습니다.');
                 location.replace('/articles/' + articleId);
            }

            function fail() {
                alert('삭제 실패했습니다.');
                location.replace('/articles/' + articleId);
            }

            httpRequest('DELETE', `/api/comments/${commentId}`, null, success, fail);
            }
        });
});


const commentModifyButton = document.getElementById('comment-modify-btn');

if (commentModifyButton) {
    commentModifyButton.addEventListener('click', event => {

        let params = new URLSearchParams(location.search);
        let commentId = params.get('commentId');
        let articleId = params.get('articleId');




        let body = JSON.stringify({
            content: document.getElementById('content').value
        });

        function success() {
            alert('수정 완료되었습니다.');
            location.replace('/articles/' + articleId);

        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace('/articles/' + articleId);
        }

        httpRequest('PUT', `/api/comments/${commentId}`, body, success, fail);
    });
}

const sortLatestButton = document.getElementById('sort-latest-btn');

if (sortLatestButton) {
    sortLatestButton.addEventListener('click', event => {
        let articleId = document.getElementById('article-id').value;

        window.location.href = '/articles/' + articleId + '?order=desc';


    });
}
const sortOldestButton = document.getElementById('sort-oldest-btn');
if (sortOldestButton) {
    sortOldestButton.addEventListener('click', event => {
        let articleId = document.getElementById('article-id').value;

        window.location.href = '/articles/' + articleId + '?order=asc';


    });
}