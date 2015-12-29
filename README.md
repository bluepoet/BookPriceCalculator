# 개요

목 오브젝트(Mock Object)의 개념을 간단한 예제를 통해 알아보는 프로젝트

# 프로젝트 설명자료

[목 오브젝트의 이해](http://)

# 개발환경

* MacBook Pro OS X Yosemite
* jdk8
* IntelliJ 14 
* lombok 1.16.6(Lombok Plugin 설치해야 함)

##### Intellij에서 lombok plugin 설치 후 확인사항

* Default Preferences > Lombok plugin > Enable Lombok plugin for this project 체크 확인
* Default Preferences > Build, Execution, Deployment > Compiler > Annotation Processors > Enable annotation processing 체크 확인

# 브랜치 설명

- develop_v0.1 : 최초 요구된 기능구현에 초점을 맞춤
- develop_v0.2 : 다양한 테스트케이스를 목 오브젝트를 DI(Dependency Injection)하는 방법으루 구현
- develop_v0.2 : Mock Framework인 Mockito를 이용하여 테스트코드 재작성함