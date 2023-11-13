import redis

"""
pip install redis即可
"""

host: str = "82.157.75.213"
port: int = 6379
password: str = "yy123456!"  # 请替换为实际密码

# 创建Redis连接
client: redis.StrictRedis = redis.StrictRedis(
    host=host, port=port, password=password, decode_responses=True
)

PREFIX_TOKEN: str = "login:token:"

"""
自己的token,先运行一次,然后前端刷新一次网页再次运行,如果第二次的结果>第一次的结果,证明headers字段绑定成功
"""
token: str = "8398efe4f6c74792a0cc5fe1788e80d8"
key: str = PREFIX_TOKEN + token
print(client.ttl(key))
client.close()
