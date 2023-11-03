"""
将xls表中的数据插入数据库的脚本,不要随便运行!
"""

from typing import Dict, List
import mysql.connector
import pandas as pd

url: str = "bj-cynosdbmysql-grp-p55yr6pq.sql.tencentcdb.com"
port: str = "29754"
user: str = "syc"
password: str = "syc123456!"
dataBase: str = "MSHD2"

mysqlConfig: Dict[str, str] = {
    "host": url,
    "port": port,
    "user": user,
    "password": password,
    "database": dataBase,
}

conn = mysql.connector.connect(**mysqlConfig)
cursor = conn.cursor()

filePath: str = "region_code.xls"
sheetNames: List[str] = [
    "region_code",
    "region_code(2)",
    "region_code(3)",
    "region_code(4)",
    "region_code(5)",
    "region_code(6)",
    "region_code(7)",
    "region_code(8)",
    "region_code(9)",
]

for sheetName in sheetNames:
    df: pd.DataFrame = pd.read_excel(filePath, sheet_name=sheetName)

    for row in df.itertuples(index=False):
        id: str = row[0]
        province: str = row[1]
        city: str = row[2]
        county: str = row[3]
        town: str = row[4]
        village: str = row[5]
        insertSql: str = f"insert into addressCode (id, province, city, county, town, village) values ('{id}', '{province}', '{city}', '{county}', '{town}', '{village}');"
        cursor.execute(insertSql)

    conn.commit()

cursor.close()
conn.close()
