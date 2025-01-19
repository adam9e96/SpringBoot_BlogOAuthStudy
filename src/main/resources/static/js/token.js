// URL에서 'token' 파라미터 값 추출
const token = searchParam('token')
console.log("추출된 토큰:", token);

if (token) {
    console.log("로컬 스토리지에 토큰 저장:", token);
    localStorage.setItem("access_token", token);
}

function searchParam(key) {
    console.log("URL 파라미터 검색 시작:", key);
    // URL의 쿼리 파라미터를 다루기 위한 URLSearchParams 객체를 생성
    let urlParams = new URLSearchParams(window.location.search);
    // 지정된 키로 파라미터 값 조회
    let value = urlParams.get(key);
    console.log("검색된 파라미터 값:", value)
    return value;
}