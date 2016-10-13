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

##### Intellij에서 Spock으로 작성된 테스트코드 실행 전 플러그인 설치

Default Preferences > Plugins에서 아래 설치유무를 확인한다

* Groovy
* Gradle
* Spock Framework Enhancements

Spock Framework Enhancements은 번들 플러그인이 아니므로 Browse repositories… 에서 검색하여 설치한다.([참고링크](http://www.sjune.net/archives/1808))


# 브랜치 설명

- develop_v0.1 : 최초 요구된 기능구현에 초점을 맞춤
- develop_v0.2 : 다양한 테스트케이스 구현을 리팩토링과 함께 진행
- develop_v0.3 : Mock Framework인 Mockito와 Testing and Specification Framework인 Spock을 이용하여 테스트코드 재작성함