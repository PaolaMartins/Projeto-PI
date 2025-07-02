# 🌿 Classificador de Folhas Saudáveis vs. Doentes usando ImageJ e Random Forest

Este projeto propõe um sistema automatizado de análise de imagens de folhas com o objetivo de classificá-las como **“saudáveis”** ou **“doentes”**, utilizando **Java com ImageJ** para extração de características e **Weka com Random Forest** para classificação supervisionada.

---

## ⚙️ Tecnologias e Ferramentas

- 🧪 **ImageJ + Java**: para processamento e extração de atributos das imagens
- 🧠 **Weka**: para treinamento, validação e aplicação de modelos de aprendizado de máquina
- 📸 **Base de dados**: imagens públicas de folhas (do Kaggle)

---

## 📊 Atributos extraídos das imagens

Para cada imagem de folha, foram extraídas três características principais:

1. **Área da folha (em pixels)** — via limiarização automática (`Threshold`)
2. **Área das manchas** — regiões escuras segmentadas com `Li dark` + `Gaussian blur`
3. **Cor média RGB** — média aritmética dos canais de cor dos pixels da folha

Esses atributos foram salvos em um arquivo `.arff` (formato do Weka), sendo a base para o treinamento do classificador.

---

## 📂 Organização dos dados

As imagens foram organizadas em duas pastas:

/imagens/
├── saudavel/
├── doente/


Um plugin Java percorre essas pastas, extrai os atributos e gera o arquivo `folhas.arff`.

---

## 🔎 Classificador usado

O modelo de aprendizado supervisionado escolhido foi o:

### ✅ Random Forest

- Algoritmo robusto contra overfitting
- Lida bem com atributos contínuos e imagens ruidosas
- Implementado e testado via Weka

**Referência:**  
Breiman, L. (2001). *Random forests*. Machine Learning, 45(1), 5–32. https://doi.org/10.1023/A:1010933404324

---

## 🧪 Treinamento e Validação

- Total de imagens: **1862**
  - 519 saudáveis
  - 1343 doentes
- Após **balanceamento com resample supervisionado**:
  - 931 saudáveis
  - 931 doentes

### 🔁 Avaliação com 10-fold cross-validation

| Métrica       | Resultado |
|---------------|-----------|
| Acurácia      | **88,7%** ✅  
| Kappa         | **0,77** (acordo substancial)  
| AUC (ROC)     | **0,94**  
| F1-score médio| **88,6%**

#### 📊 Precisão e Recall por classe:
| Classe    | Precisão | Recall | F1-Score |
|-----------|----------|--------|----------|
| Saudável  | 85,7%    | 92,8%  | 89,1%    |
| Doente    | 92,2%    | 84,5%  | 88,2%    |

---

## 🧩 Plugin no ImageJ

O sistema final foi encapsulado em um **plugin Java para ImageJ**:

- O usuário abre uma imagem no ImageJ
- O plugin extrai os mesmos atributos usados no treinamento
- O modelo `.model` salvo pelo Weka é carregado
- A imagem é classificada como **“Saudável” ou “Doente”**, com log dos dados

---

## ⚠️ Limitações

- Apenas três atributos foram utilizados (área, mancha e cor média)
- O modelo é sensível a **iluminação e qualidade das imagens**
- Pequenas imperfeições ou ruídos podem causar **erros de classificação (~12%)**

---

## ✅ Pontos fortes

- Alta acurácia mesmo com atributos simples
- Plugin funcional, integrado ao ImageJ
- Base realista e balanceada para treino
- Fácil expansão com novos atributos (ex: textura, redes neurais)

---

## 📎 Links úteis

- 🔗 [Base de dados de folhas - Kaggle](https://www.kaggle.com/datasets/csafrit2/plant-leaves-for-imageclassification)
- 🔗 [Random Forest - Artigo original (Breiman, 2001)](https://doi.org/10.1023/A:1010933404324)
- 🔗 [Weka - Ferramenta de aprendizado de máquina](https://www.cs.waikato.ac.nz/ml/weka/)
- 🔗 [ImageJ - Editor científico de imagens](https://imagej.nih.gov/ij/)

---

## 👩‍💻 Autoras

- Naihara Barboza S. dos Santos  
- Paola Martins da Silva  
- Docente orientador: Prof. Luiz Otávio Murta Júnior

---


