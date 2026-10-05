# VitaSync para Android

MVP nativo para Android, desenvolvido em Kotlin e Jetpack Compose. A interface está em português e reúne educação em saúde cotidiana, movimento, alimentação, hidratação, descanso e um diário manual.

## Requisitos

- Android Studio com Android SDK 35 instalado
- JDK 17
- Gradle 8.9 (Android Gradle Plugin 8.7.3)

Abra este diretório no Android Studio ou compile pela linha de comando:

```sh
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

O APK de depuração será criado em `app/build/outputs/apk/debug/app-debug.apk`. Para instalar em um dispositivo conectado com depuração USB ativada, use `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

## O que está incluído

- Painel diário com cartões para os quatro pilares, progresso de lições e acesso ao diário.
- Lições educativas curtas em sequência com indicador de progresso.
- Sugestões simples de treino em casa ou academia, com marcação manual de conclusão.
- Receitas acessíveis, registro manual de refeições e botão rápido de hidratação em incrementos de 250 ml.
- Check-in manual de horas de sono, refeições, água e atividade; sugestões de rotina contextualizadas pelo sono registrado, sem alegações médicas.
- Preferências de privacidade, exportação JSON pelo seletor de arquivos Android e exclusão dos dados locais.

## Dados e limites do MVP

Os registros do dia e a preferência de local de treino são mantidos em `SharedPreferences` no próprio aparelho; registros diários são reiniciados quando um novo dia é detectado e o progresso das lições é preservado. O app não possui backend, conta, telemetria ou envio remoto de dados de saúde. O app não integra com Health Connect nesta versão; não solicita permissões de saúde nem simula sincronização. A exclusão remove os registros e o progresso mantidos pelo VitaSync neste aparelho.

Neste ambiente não foram encontrados JDK nem Android SDK, então não foi possível executar os testes/build nem gerar o APK. Instale os requisitos acima para executar os comandos de build.
