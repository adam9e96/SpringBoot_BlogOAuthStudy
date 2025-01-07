'use strict';

/**
 * 삭제 기능을 담당하는 코드입니다.
 * deleteButton 요소를 찾아 클릭 이벤트 리스너를 추가합니다.
 */
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    console.log("삭제버튼 js 실행")
    deleteButton.addEventListener("click", (event) => {
        console.log("현 게시글의 id", document.getElementById("article-id").value);
        let id = document.getElementById("article-id").value;

        function success() {
            alert("글이 삭제되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("글 삭제에 실패했습니다.");
            location.replace("/articles");
        }

        httpRequest("DELETE", "/api/articles/" + id, null, success, fail);
    });
}


/**
 * 수정 기능을 담당하는 코드입니다.
 * modifyButton 요소를 찾아 클릭 이벤트 리스너를 추가합니다.
 * 수정 시 제목과 내용을 가져와서 PUT 요청을 보냅니다.
 * 요청이 완료되면 알림을 표시하고 해당 글 페이지로 이동합니다.
 */
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    console.log("수정버튼 js 실행")
    modifyButton.addEventListener("click", (event) => {
        let params = new URLSearchParams(location.search);
        let id = params.get("id");

        const body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success() {
            alert("글이 수정되었습니다.");
            location.replace("/articles/" + id);
        }

        function fail() {
            alert("글 수정에 실패했습니다.");
            location.replace("/articles/" + id);
        }

        httpRequest("PUT", "/api/articles/" + id, body, success, fail);
    });
}


// 등록 기능
// id 가 create-btn 인 엘리먼트
const createButton = document.getElementById('create-btn');

if (createButton) {
    console.log("등록버튼 js 실행")
    // 클릭 이벤트가 감지되면 등록 API 요청
    // 등록 버튼을 클릭하면 /api/articles 로 POST 요청을 보냅니다.
    createButton.addEventListener("click", (event) => {
        const body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success() {
            alert("글이 등록되었습니다.");
            location.replace("/articles");
        }

        function fail() {
            alert("글 등록에 실패했습니다.");
            location.replace("/articles")
        }

        httpRequest("POST", "/api/articles", body, success, fail);
    });
}

/// 쿠키를 가져오는 함수
function getCookie(key) {
    let result = null;
    let cookie = document.cookie.split("; ");
    cookie.some(function (item) {
        item = item.replace(" ", "");

        let dic = item.split("=");

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });
    return result;
}

// HTTP 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {
    console.log(`Sending ${method} request to ${url} with body: `, body);
    fetch(url, {
        method: method,
        headers: {
            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body,
    }).then((response) => {
        console.log(`받은 응답 상태 : ${response.status}`);
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie("refresh_token");
        if (response.status === 401 && refresh_token) {
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
                .then((res) => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then((result) => {
                    // 재발급이 성공하면 로컬 스토리지값을 새로운 액세스 토큰으로 교체
                    localStorage.setItem("access_token", result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch((error) => fail());
        } else {
            return fail();
        }
    });
}