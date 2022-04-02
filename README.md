# #######云上環境
需要配置的文件#######
cloud
# pathConfig.properties 文件注释cloud 打开dev+163
按注释切换代码
# FilesOperationServiceImpl.java 文件 --pts上传和动态表示プロジェクト文件上传共通
fileSaveRemote方法,切换数据桶代码
# PriorityOrderMstServiceImpl.java 文件 --无效
# getPtsFileDownLoad方法，切换数据桶代码



# #######本地开发+163環境需要配置的文件#######
test
# pathConfig.properties 文件注释dev+163 打开cloud
dev+163
# FilesOperationServiceImpl.java 文件
fileSaveRemote方法,切换物理机代码
# PriorityOrderMstServiceImpl.java 文件  --无效
# getPtsFileDownLoad方法，切换物理机代码

# 本地调试環境变量
```$xslt
PLANO_URL=jdbc:postgresql://10.100.1.161:54332/PLANOCYCLE?characterEncoding=UTF-8&useSSL=false
PLANO_USERNAME=admin
PLANO_PASSWORD=admin1234
loggingFilePath=D:\files
PORTAL_URL=http://10.100.1.161:8102/planocycleweb/
projectIds=nothing
bucketNames=nothing
ptsDownPath=http://10.100.1.161:8102/planocycleweb/
productDownPath=D:\files
smartUrlPath=http://10.100.1.161:12001/suntorylink/planocycle/CGI/
```