# Jenkins CICD 프로젝트



### 설치 및 구성 메뉴얼

1. GitHub repo create
    - MZ-docker  [Private] : docker 가상 서버 2개
    - MZ-web [Public] : Jenkinsfile, Dockerfile, deploy.yaml, web source
    
2. VirtualBox server create
    
    2-1. vagrantfile
    
    ```groovy
    $script = <<-SCRIPT
    sudo apt-get update -y
    sudo apt-get install -y ca-certificates curl gnupg
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
      "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo usermod -a -G docker vagrant
    docker login -u ehszl409 --password-stdin < /vagrant/env/docker_token
    SCRIPT
    
    Vagrant.configure("2") do |config|
      config.vm.box = "ubuntu/focal64"
    
      config.vm.define "docker" do |docker|
        docker.vm.hostname = "docker"
        docker.vm.provider "virtualbox" do |vb|
          vb.name = "docker"
          vb.cpus = 4
          vb.memory = 8192
        end
        docker.vm.network "private_network", ip: "192.168.10.2"
        docker.vm.provision "shell", inline: $script
      end
      config.vm.define "dockerRepo" do |dockerRepo|
        dockerRepo.vm.hostname = "dockerRepo"
        dockerRepo.vm.provider "virtualbox" do |vb|
          vb.name = "dockerRepo"
          vb.cpus = 2
          vb.memory = 4096
        end
        dockerRepo.vm.network "private_network", ip: "192.168.10.3"
        dockerRepo.vm.provision "shell", inline: $script
      end
    end
    ```
    
    2-2. virtual server create 
    
    ```groovy
    vagrant up docker (cpu 4 / memory 8192 / 192.168.10.2)
    vagrant up dockerRepo (cpu 2 / memory 4096 / 192.168.10.3)
    ```
    
3. docker server : volume + docker + jenkins imgae container create 
    
    3-1. jenkins container create
    
    ```bash
    docker volume create jenkins-volume
    # docker 그룹 번호 확인
    sudo cat /etc/group | grep docker
    
    docker run -it -d -p 8080:8080 --name jenkins \
    -v jenkins-volume:/var/jenkins_home/ \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v $(which docker):/usr/bin/docker \
    --group-add 998 jenkins/jenkins:2.387.2-lts
    
    # 젠킨스 
    docker exec -t jenkins /bin/bash -c "cat /var/jenkins_home/secrets/initialAdminPassword"
    ```
    
    - virtualBox 에서 8080 포트 포워딩 설정을 해줘야 한다.
    
4. ngrok install & setting
    
    ```groovy
    curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null && echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | sudo tee /etc/apt/sources.list.d/ngrok.list && sudo apt update && sudo apt install ngrok
    ngrok config add-authtoken 2SgbBa9JCgr5zRoNgyX1p3HqMbP_4WBidbGdm5JdMjSAkLb9U
    ngrok http 8080
    ```
    
    [](https://5685-218-235-89-202.ngrok-free.app/)
    
5. github webhook test
    
    5-1. credential create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled.png)
    
    5-2. github webhook create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%201.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%202.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%203.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%204.png)
    
6. jenkins pipeline
    
    6-1. create item
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%205.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%206.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%207.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%208.png)
    
    6-2. pipeline configure
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%209.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2010.png)
    
7. jenkinsfile maven build 
    
    7-1. plugin install
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2011.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2012.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2013.png)
    
    7-2. docker hub credential create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2014.png)
    
    7-3. jenkinsfile script
    
    ```groovy
    				stage('maven build, test, packaging(war)') {
                agent {
                    docker {
                        image 'maven:3.8.3-openjdk-17'
                        reuseNode true
                        registryUrl 'https://index.docker.io/v1/'
                        registryCredentialsId 'docker-hub'
                    }
                }
                    steps {
                        sh 'mvn clean install'
                    }     
            }
    ```
    
8. sonarqube server create
    
    8-1. 포트 포워딩
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2015.png)
    
    8-2. 컨테이너 생성
    
    ```groovy
    docker pull sonarqube
    docker run -itd -p 9000:9000 --name=sonar sonarqube:latest 
    ```
    
    8-3. project create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2016.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2017.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2018.png)
    
    8-4. webhook connection
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2019.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2020.png)
    
    8-5. token create
    
    ![usertoken create](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2021.png)
    
    usertoken create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2022.png)
    
    ![projecttoken create](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2023.png)
    
    projecttoken create
    
    8-6. jenkins plugin configure
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2024.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2025.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2026.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2027.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2028.png)
    
    8-7. jenkinsfile
    
    ```groovy
    stage('sonarQube testing') {
                steps {
                    script {
                        withSonarQubeEnv('sonarproject') {
                            sh """
                                docker run --rm \
                                    -e SONAR_HOST_URL=$SONAR_HOST_URL \
                                    -e SONAR_LOGIN=$SONAR_AUTH_TOKEN \
                                    -e SONAR_SCANNER_OPTS='-Dsonar.projectKey=sonarproject -Dsonar.java.binaries=./target' \
                                    -v /var/lib/docker/volumes/jenkins-volume/_data/workspace/spring-cicd:/usr/src \
                                    sonarsource/sonar-scanner-cli
                            """
                        }
                    }
                    timeout(time: 3, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }        
            }
    ```
    
    8-8. quility check
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2029.png)
    
9. jenkinsfile github release 
    
    9-1. plugin install
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2030.png)
    
    9-2. jenkinsfile
    
    ```groovy
    stage('github create release') {
                steps {
                    script { def response = sh(script: """                    
                            curl -sSL \
                                  -X POST \
                                  -H "Accept: application/vnd.github+json" \
                                  -H "Authorization: Bearer ${GC_PSW}" \
                                  -H "X-GitHub-Api-Version: 2022-11-28" \
                                  https://api.github.com/repos/${GIT_USERNAME}/${GIT_REPO}/releases \
                                  -d '{
                                          "tag_name":"${TAG_VERSION}",
                                          "target_commitish":"main",
                                          "name":"${TAG_VERSION}",
                                          "body":"Description of the release",
                                          "draft":false,
                                          "prerelease":false,
                                          "generate_release_notes":false
                                        }'
                        """, returnStdout: true)
    
                        def json = readJSON text: "$response"
                        def id = json.id
    
                        sh "mv target/demo-0.0.1-SNAPSHOT.war ${GIT_REPO}-${TAG_VERSION}.war"
                            
                        sh """
                            curl -sSL \
                                -X POST \
                                -H "Accept: application/vnd.github+json" \
                                -H "Authorization: Bearer ${GC_PSW}" \
                                -H "X-GitHub-Api-Version: 2022-11-28" \
                                -H "Content-Type: application/octet-stream" \
                                "https://uploads.github.com/repos/${GIT_USERNAME}/${GIT_REPO}/releases/${id}/assets?name=${GIT_REPO}-${TAG_VERSION}.war" \
                                --data-binary "@${GIT_REPO}-${TAG_VERSION}.war"
                        """
                        sh "mv ${GIT_REPO}-${TAG_VERSION}.war ROOT.war"
                    }
                }
            }
    ```
    
10. dockerhub build & push
    
    10-1. credential create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2031.png)
    
    Username : dockerhub 의 자신의 ID (정확한 값)
    
    password : dockerhub 에서 read write 가능한 토큰 값 (정확한 값)
    
    ID : (임의의 값 하지만 변수나 파라미터로 호출 가능한 값)
    
    Description : (임의의 값)
    
    10-2. dockerfile
    
    ```yaml
    FROM tomcat:10.0-jdk17
    
    COPY ROOT.war /usr/local/tomcat/webapps/
    
    EXPOSE 8080
    ```
    
    10-3. jenkinsfile
    
    ```groovy
    stage('dockerfile build'){
                steps{
                    script{
                        docker.withRegistry('https://index.docker.io/v1/', 'docker-hub') {
                            docker.build("ehszl409/springweb:${TAG_VERSION}", "--no-cache ./")    
                        }
                    }
                }
            }
            stage('dockerHub push'){
                steps{
                    script{
                        docker.withRegistry('https://index.docker.io/v1/', 'docker-hub'){
                            def img = docker.image("ehszl409/springweb:${TAG_VERSION}")
                            img.push("${TAG_VERSION}")
                            img.push('latest')
                        }
                    }
                }
            }
    ```
    
    10-4. dockerHub push test
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2032.png)
    
11. docker registry
    
    11-1. dockerRepo server registry container create
    
    ```bash
    docker run -d -p 5000:5000 --restart=always --name image_repo registry
    sudo vi /etc/docker/daemon.json
    
    # daemon.json 파일 속
    {
            "insecure-registries": ["http://localhost:5000"]
    }
    ```
    
    11-2. docker daemon remote configure
    
    ```bash
    sudo vi /etc/systemd/system/multi-user.target.wants/docker.service
    > ExecStart 부분 제일 뒤에 -H 서버 IP 작성
    
    sudo systemctl daemon-reload
    sudo systemctl restart docker
    
    # 명령어 전달 방법
    docker -H tcp://<dockerserver IP>:2375 or <할당된port> (docker 생략가능) ps
    
    # registry 저장된 이미지 확인 경로
    ls -la /var/lib/registry/docker/registry/v2/repositories/
    ```
    
    11-3. jenkinsfile
    
    ```groovy
    stage('dockerRepo push'){
                steps{
                    sh "docker -H tcp://192.168.10.3:2375 pull ehszl409/springweb:${TAG_VERSION}"
                    sh "docker -H tcp://192.168.10.3:2375 tag ehszl409/springweb:${TAG_VERSION} localhost:5000/springweb:${TAG_VERSION}"
                    sh "docker -H tcp://192.168.10.3:2375 tag ehszl409/springweb:${TAG_VERSION} localhost:5000/springweb:latest"
                    sh "docker -H tcp://192.168.10.3:2375 push localhost:5000/springweb:${TAG_VERSION}"
                    sh "docker -H tcp://192.168.10.3:2375 push localhost:5000/springweb:latest"
                    sh "docker -H tcp://192.168.10.3:2375 rmi localhost:5000/springweb:${TAG_VERSION}"
                    sh "docker -H tcp://192.168.10.3:2375 rmi localhost:5000/springweb:latest"
                    sh "docker -H tcp://192.168.10.3:2375 rmi ehszl409/springweb:${TAG_VERSION}"
                    sh "docker rmi ehszl409/springweb:${TAG_VERSION}"
                    sh "docker rmi ehszl409/springweb:latest"
                }
    ```
    
12. jenkins k8s connections 
    
    12-1. kubernetes install & configure
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2033.png)
    
    ```bash
    ############## 컨트롤러 + 워커 노드 설정 #####################
    
    # 업데이트 및 재부팅(모든 서버)
    sudo apt-get update && sudo apt-get -y full-upgrade
    sudo reboot -f
    
    # 설치 전 필요한 패키지 설치(모든 서버)
    sudo apt-get -y install curl gnupg2 software-properties-common apt-transport-https ca-certificates
    
    # 도커 리포지터리 등록(모든 서버)
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    
    # 쿠버네티스 리포지터리 등록(모든 서버)
    curl -fsSL https://dl.k8s.io/apt/doc/apt-key.gpg | sudo apt-key add -
    echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
    
    # 리포지토리 업데이트 진행(모든 서버)
    sudo apt update -y
    
    # 컨테이너 런타임 설치(모든 서버)
    sudo apt install -y containerd.io
    sudo mkdir -p /etc/containerd
    sudo containerd config default | sudo tee /etc/containerd/config.toml > /dev/null
    
    # 쿠버네티스 설치(모든 서버)
    sudo apt -y install kubelet kubeadm kubectl
    sudo apt-mark hold kubelet kubeadm kubectl
    
    # 스왑 비활성화(모든 서버)
    sudo sed -i '/swap/s/^/#/' /etc/fstab
    sudo swapoff -a && sudo mount -a
    
    # 커널 기능 설정 변경(모든 서버)
    sudo su - -c "echo 'net.bridge.bridge-nf-call-ip6tables = 1' >> /etc/sysctl.d/kubernetes.conf"
    sudo su - -c "echo 'net.bridge.bridge-nf-call-iptables = 1' >> /etc/sysctl.d/kubernetes.conf"
    sudo su - -c "echo 'net.ipv4.ip_forward = 1' >> /etc/sysctl.d/kubernetes.conf"
    
    # 커널 모듈 로드 설정(모든 서버)
    sudo su - -c "echo 'overlay' >> /etc/modules-load.d/containerd.conf"
    sudo su - -c "echo 'br_netfilter' >> /etc/modules-load.d/containerd.conf"
    
    # 커널 모듈 및 설정 활성화(모든 서버)
    sudo modprobe overlay
    sudo modprobe br_netfilter
    sudo sysctl --system
    
    # 서비스 활성화(모든 서버)
    sudo systemctl restart containerd kubelet
    sudo systemctl enable containerd kubelet
    
    # 모든 서버의 /etc/hosts 파일 수정
    # 모든 서버에 컨트롤러 및 워커 노드의 IP 주소와 hostname 정보를 등록
    sudo vi /etc/hosts
    	192.168.10.11 kube-controller
    	192.168.10.12 kube-worker-node1
    	192.168.10.13 kube-worker-node2
    
    ################# 컨트롤러 설정 #################
    # 컨트롤러 노드에 쿠버네티스 관련 서비스 이미지 설치
    sudo kubeadm config images pull
    
    # kubeadm 으로 부트스트랩 진행
    sudo kubeadm init --apiserver-advertise-address 192.168.10.11 --pod-network-cidr 172.30.0.0/16 --upload-certs --control-plane-endpoint kube-controller
    	# 워커 노드를 참가 시키는 키 (워커 노드 서버에서 입력)
    	sudo kubeadm join kube-controller:6443 --token hkoe6o.xonqaags91wcjiir --discovery-token-ca-cert-hash sha256:ae40608c56e145fd07f9a8d1ebb177ded14e51a384df3687517a095fbe43c137
    	
    
    # kubectl 명령어를 사용할 수 있게 하는 설정
    mkdir -p $HOME/.kube
    sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
    sudo chown $(id -u):$(id -g) $HOME/.kube/config
    
    # 쿠버네티스의 컨테이너 네트워크 인터페이스 설치 및 설치 파일 다운로드 (CNI)
    kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.26.1/manifests/tigera-operator.yaml
    wget https://raw.githubusercontent.com/projectcalico/calico/v3.26.1/manifests/custom-resources.yaml
    
    # 네트워크 설정 변경 후 설치
    sed -i 's/cidr: 192\.168\.0\.0\/16/cidr: 172\.30\.0\.0\/16/g' custom-resources.yaml
    sed -i '7a\  registry: quay.io/' custom-resources.yaml #2023 08 19 수정 본 에러 안뜨는지 확인 바람
    kubectl create -f custom-resources.yaml
    
    !! 에러 
    # kubectl create -f custom-resources.yaml 이후 아래와 같은 에러 발생시
    error: error parsing custom-resources.yaml: error converting YAML to JSON: yaml: line 9: did not find expected key
    
    # 컨트롤러에서 해당 라인을 건너 뛰고 kubectl create -f custom-resources.yaml 실행
    sed -i '7a registry: quay.io/' custom-resources.yaml
    !!
    
    # 최종 확인
    kubectl get nodes
    watch -n 1 kubectl get pods --all-namespaces
    
    ################################################
    
    # kubeadm init 을 다시 할 경우 (컨트롤러 워커노드 모두)
    sudo kubeadm reset
    sudo rm -rf /etc/cni/net.d/
    # 이후 image pull 부터 차례대로 실행
    
    ################### Tab 자동 완성 활성화 ##################
    sudo apt-get install bash-completion
    source <(kubectl completion bash)
    source <(kubeadm completion bash)
    echo 'source <(kubectl completion bash)' >>~/.bashrc
    echo 'source <(kubeadm completion bash)' >>~/.bashrc
    
    ################## 도커 허브에서 이미지 Pull하기 위한 k8s secret 만들기 ############################
    # kubectl create secret
    # 도커 레지스트리에 접근하기 위해 secret을 만들어준다.
    # 도커를 직접 설치하지 않았기 때문에 이런 방식을 사용하는 것
    kubectl create secret docker-registry docker-pull-secret \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=ehszl409 \
    --docker-password= *
    ```
    
    12-1-a. ingress install
    
    [https://github.com/kubernetes/ingress-nginx](https://github.com/kubernetes/ingress-nginx)
    
    ```bash
    git clone https://github.com/kubernetes/ingress-nginx.git
    kubectl apply -k ingress-nginx/deploy/static/provider/baremetal
    kubectl describe ingresses.networking.k8s.io myingress
    sudo vi /etc/hosts
    	192.168.10.12 example.com
    
    curl http://example.com:<nodePort>/<prefix>
    
    ```
    
    12-2. plugin install
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2034.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2035.png)
    
    12-3. credential create
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2036.png)
    
    12-4. jenkins container hosts file configure
    
    ```bash
    docker exec -it -u root jenkins sh -c 'echo "192.168.10.11 kube-controller" >> /etc/hosts'
    ```
    
    12-5. (optional) k8s jenkins connection testing
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2037.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2038.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2039.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2040.png)
    
    ![Untitled](%E1%84%89%E1%85%A5%E1%86%AF%E1%84%8E%E1%85%B5%20%E1%84%86%E1%85%B5%E1%86%BE%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%20%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%E1%84%8B%E1%85%A5%E1%86%AF%20(1)%201290284436694191b46d35097535c038/Untitled%2041.png)
    
    12-6. jenkinsfile
    
    ```groovy
    stage ('k8s deploy'){
                steps{
                    script {
                        sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl"'
                        sh 'chmod +x kubectl'
                        withKubeConfig([credentialsId: 'admin.conf', serverUrl: 'https://kube-controller:6443'])
                        { 
    												// 특정 버전 이미지를 PULL 하는 코드
                            //sh "sed -i 's/springweb:.*/springweb:${TAG_VERSION}/g' springweb.yaml"
                            sh './kubectl apply -f springweb.yaml'
                        }
                    }
                }
            }
    ```
    
    12-7. springweb.yaml
    
    ```bash
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: tomcat-server
    spec:
      selector:
        matchLabels:
          app: first-app
      replicas: 2
      template:
        metadata:
          labels:
            app: first-app
        spec:
          containers:
          - name: test
            image: ehszl409/springweb:latest
            resources:
              limits: 
                memory: "512Mi"
                cpu: "500m"
            ports:
            - containerPort: 8080
          imagePullSecrets:
          - name: docker-pull-secret
              
    # 세 번째 리소스: first-service Service 생성
    ---
    apiVersion: v1
    kind: Service
    metadata:
      name: first-service                 # 서비스 이름
    spec:
      selector:
        app: first-app                    # first-app 레이블이 붙은 Pod를 선택
      ports:
      - port: 80                           # 서비스 포트 설정 (80번 포트 사용)
        targetPort: 8080                    # 선택된 Pod의 대상 포트 설정 (80번 포트 사용)
    
    # 다섯 번째 리소스: myingress Ingress 리소스 생성
    ---
    apiVersion: networking.k8s.io/v1
    kind: Ingress
    metadata:
      name: myingress                     # Ingress 리소스의 이름
      labels:
        name: myingress                   # Ingress 리소스의 레이블
      annotations:
        # Nginx Ingress가 처리할 때, 요청을 받으면 URI를 다시 쓰기 위한 설정 (경로를 변경할 수 있음
        nginx.ingress.kubernetes.io/rewrite-target: "/"
        # Ingress 리소스를 처리할 Ingress Controller (여기서는 nginx Ingress Controller를 사용)
    spec:
      ingressClassName: nginx
      rules:
      - host: "home.com"               # 호스트 이름 설정 (example.com)
        http:
          paths:
          - pathType: Prefix               # 경로 유형 설정 (접두사)
            path: /                # "/first"로 시작하는 경로에 대해 처리함
            backend:
              service:
                name: first-service        # 연결할 서비스 이름 (first-service)
                port:
                  number: 80               # 대상 서비스 포트 번호 (80)
    ```