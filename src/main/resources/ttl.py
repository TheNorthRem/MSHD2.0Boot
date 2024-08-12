import redis

"""
pip install redis即可
"""

host: str = "xxx.xxx.xxx.xxx"
port: int = 1234
password: str = "xxxxxx"  # 请替换为实际密码

# 创建Redis连接
client: redis.StrictRedis = redis.StrictRedis(
    host=host, port=port, password=password, decode_responses=True
)

PREFIX_TOKEN: str = "login:token:"

"""
自己的token,先运行一次,然后前端刷新一次网页再次运行,如果第二次的结果>第一次的结果,证明headers字段绑定成功
"""
token: str = "5a56215c06ea4e19b14fc75d18776f84"
key: str = PREFIX_TOKEN + token
print(client.ttl(key))
client.close()
