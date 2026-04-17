#  Space Shooter 

![LibGDX](https://img.shields.io/badge/LibGDX-1.12.1-red.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)

**Space Shooter** é um jogo de nave arcade frenético, focado em performance, gameplay fluida e feedbacks visuais intensos. Desenvolvido em Java com o framework **LibGDX**, este projeto foi transformado de um protótipo básico em uma experiência completa de jogo.

---

##  Principais Funcionalidades

###  Gameplay e Combate
*   **Sistema de Combo**: Multiplicadores de pontuação ao destruir inimigos rapidamente.
*   **Progressão de Armas**: 4 níveis de upgrade para sua metralhadora laser.
*   **Defesa de Escudo**: Power-up de escudo azul que absorve até 3 impactos.
*   **Ataque Especial (SP)**: Barra de energia que permite disparar um "Tiro Fodão" devastador (Tecla `N`).

###  Campanha e Estrutura
*   **Sistema de Ondas (Waves)**: Enfrente hordas de inimigos com dificuldade crescente.
*   **O Dreadnought (Boss)**: Um chefe gigante surge a cada **500 pontos** com padrões de ataque únicos.
*   **Contagem Regressiva**: Intervalos de 5 segundos entre as ondas para preparação estratégica.

###  Visual e Game Feel
*   **Parallax Background**: Estrelas com múltiplas camadas de profundidade.
*   **Sistema de Partículas**: Rastros de motor (Engine Trails) e explosões vibrantes.
*   **Feedback de Impacto**: Efeitos de Screen Shake (tremer a tela) e Flash Feedback ao levar ou causar dano.
*   **UI Profissional**: Interface moderna usando `Scene2D`, incluindo persistência de recordes (High Scores).

---

##  Arquitetura e Performance

O projeto segue as melhores práticas de desenvolvimento de jogos em Java:
*   **Object Pooling**: Reutilização de balas, inimigos e efeitos para evitar Garbage Collection e garantir 60 FPS estáveis.
*   **AssetManager**: Gerenciamento centralizado e assíncrono de texturas, sons e skins.
*   **Clean Code**: Separação clara de responsabilidades entre telas, entidades e lógica de gerenciamento.
*   **State Management**: Sistema robusto de gerenciamento de telas (Menu, Gameplay, Game Over).

---

##  Controles

| Ação | Tecla |
| :--- | :--- |
| **Movimentação** | Setas (Cima, Baixo, Esquerda, Direita) |
| **Atirar** | `Espaço` |
| **Ataque Especial** | `N` (Requer 100 SP) |
| **Pausar/Sair** | `ESC` |

---

##  Como Executar

### Pré-requisitos
*   JDK 17 ou superior instalado.
*   Gradle 8.x (opcional, incluído no wrapper).

### Passos
1. Clone este repositório.
2. No diretório raiz, execute:
   ```bash
   ./gradlew lwjgl3:run
   ```

---

## 👨‍💻 Créditos
Desenvolvido como uma evolução de um projeto de fundamentos Java, focado em transformar código iniciante em uma arquitetura de game engine profissional.

---

> *"No espaço, ninguém ouve o som dos seus combos... mas eles sentem o impacto!"* 🛸💥
