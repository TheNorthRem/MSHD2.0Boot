import axios from 'axios';

// 构建请求URL，包括查询参数
const url = 'http://localhost:8077/Disaster/ListDisasters';

axios.interceptors.request.use(
    config => {
        config.headers['authorization'] = '26c92cc7566c484e8e110c756b9f571d'
        return config
    },
    error => {
        console.log(error)
        return Promise.reject(error)
    }
)

// 发送GET请求并附带查询参数
axios.get(url)
    .then(response => {
        // 处理响应数据
        console.log(response.data);
    })
    .catch(error => {
        // 处理错误
        console.error('发生错误:', error);
    });
