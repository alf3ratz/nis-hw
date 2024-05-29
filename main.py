import random
import numpy as np
from PIL import Image, ImageDraw


# Генерация случайного генотипа
def generate_genotype():

    # Гены
    skeleton_genes = [random.randint(-9, 9) for _ in range(15)]
    random.shuffle(skeleton_genes)

    # Длина
    length_gene = random.randint(2, 12)
    genotype = skeleton_genes + [length_gene]
    return genotype

# Рисуем биоморфу из генов
def generate_phenotype(genotype, size=(150, 150)):
    image = Image.new("L", (150, 150))
    draw = ImageDraw. Draw(image)
    center = (size[0] // 2, size[1] // 2)
    angle = 0
    for i, gene in enumerate(genotype[:-1]):
        if i < 7:
            length = gene * 10
            angle += genotype[(i + 1) % 7] * 10
            end_x = int(center[0] + length * np.cos(np.radians(angle)))
            end_y = int(center[1] + length * np.sin(np.radians(angle)))
            draw.line([center, (end_x, end_y)], fill=255, width=1)
    return image

# Подсчет подобия
def similarity(biomorph1, biomorph2):
    array1 = np.array(biomorph1)
    array2 = np.array(biomorph2)

    # Нормализация массивов
    norm_array1 = (array1 - array1.mean()) / (array1.std() * len(array1))
    norm_array2 = (array2 - array2.mean()) / (array2.std())

    # кросс-корреляция
    correlation = np.sum(norm_array1 * norm_array2)
    return correlation

def evolution(target_biomorph, population_size=100, generations=10):
    # Генерация начальной популяции
    population = [generate_genotype() for _ in range(population_size)]
    best_similarity = 0
    unchanged_generations = 0

    for generation in range(generations):
        # оценка подобия с целевой биоморфой
        similarities = [similarity(generate_phenotype(biomorph), target_biomorph) for biomorph in population]
        max_similarity = max(similarities)
        max_index = similarities.index(max_similarity)

        # прекращение эволюции
        if max_similarity <= best_similarity:
            unchanged_generations += 1
            if unchanged_generations >= 10:
                break
        else:
            best_similarity = max_similarity
            unchanged_generations = 0

        # Выбор наиболее подходящих биоморф
        selected_genotypes = [population[i] for i in range(population_size) if similarities[i] == max_similarity]

        # Мутации
        new_population = []
        for genotype in selected_genotypes:
            mutated_genotypes = [genotype[:i] + [genotype[i] + random.choice([-1, 1])] + genotype[i+1:] for i in range(len(genotype) - 1)]
            new_population.extend(mutated_genotypes)

        # Дополнение новой популяции случайными генотипами
        while len(new_population) < population_size:
            new_population.append(generate_genotype())

        population = new_population

    return generate_phenotype(population[max_index]), best_similarity

if __name__ == "__main__":
    # Задаем целевую биоморфу (например, котика)
    target_biomorph = generate_phenotype(generate_genotype())
    target_biomorph.show()

    # Запуск эволюции
    evolved_biomorph, similarity_score = evolution(target_biomorph)
    evolved_biomorph.show()
