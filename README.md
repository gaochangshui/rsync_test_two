# #######云上環境
需要配置的文件#######
cloud
# pathConfig.properties 文件注釋cloud 打開dev+163
按注釋切換代碼
# FilesOperationServiceImpl.java 文件 --pts上傳和動態表示プロジェクト文件上傳共通
fileSaveRemote方法,切換数据桶代碼
# PriorityOrderMstServiceImpl.java 文件 --无效
# getPtsFileDownLoad方法，切換数据桶代碼



# #######本地開發+163環境需要配置的文件#######
test
# pathConfig.properties 文件注釋dev+163 打開cloud
dev+163
# FilesOperationServiceImpl.java 文件
fileSaveRemote方法,切換物理机代碼
# PriorityOrderMstServiceImpl.java 文件  --无效
# getPtsFileDownLoad方法，切換物理机代碼

# 本地調試環境変量
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