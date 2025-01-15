'use strict';
const token = searchParam('token');
console.log("토큰 찾음:", token);
if (token) {
    console.log("localStorage에 토큰 저장");
    localStorage.setItem("access_token", token);
}

function searchParam(key) {
    console.log("Storing token in localStorage");
    let urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(key);
}