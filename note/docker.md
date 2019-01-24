## docker基本操作
#### 1、移除旧版本的docker
```
sudo yum remove docker \
                docker-client \
                docker-client-latest \
                docker-common \
                docker-latest \
                docker-latest-logrotate \
                docker-logrotate \
                docker-engine
```
#### 2、安装docker-ce
```
sudo yum list docker-ce --showduplicates | sort -r
sudo yum install docker-ce:<version>
```
#### 3、开机启动开/关
```
sudo systemctl enable docker
sudo systemctl disable docker
```
#### 4、导出/导入镜像
```
docker save -o <name>.tar.gz <image>:<tag>
docker load -i <name>.tar.gz -q
```
#### 5、启动/停止容器
```
docker run -d -p <开放port>:<容器内port> --name <name> <image>
docker container start containerid
docker container stop containerid
```
#### 6、镜像上传到github
+ 登录github
```
docker login
```
+ 创建tag
```
docker tag <image>[:<tag>] <dockerhub_username>/<target_name>[:tag]
```
+ 上传镜像
```
docker push <dockerhub_username>/<target_name>[:tag]
```
+ 下载镜像
```
docker pull <dockerhub_username>/<target_name>[:tag]
```
