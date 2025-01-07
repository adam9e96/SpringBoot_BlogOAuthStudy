'use strict';
const token = searchParam('token');

if (token) {
    localStorage.setItem("access_token", token);
}

function searchParam(key) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(key);
}