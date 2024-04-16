package org.example;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

enum Gender {
    MALE,
    FEMALE
}

class Person {
    private String name;
    private Gender gender;

    public Person(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }
}

enum Relationship {
    SPOUSE,
    PARENT_CHILD
}

class Pair<K, V> {
    private K left;
    private V right;

    public Pair(K left, V right) {
        this.left = left;
        this.right = right;
    }

    public K getLeft() {
        return left;
    }

    public V getRight() {
        return right;
    }


}

public class FamilyTree {
    private Map<String, Set<Pair<String, Relationship>>> relationships;

    public FamilyTree() {
        relationships = new HashMap<>();
    }

    public void addRelationship(String person1, String person2, Relationship relationship) {
        relationships.computeIfAbsent(person1, k -> new HashSet<>())
                .add(new Pair<>(person2, relationship));
        if (relationship == Relationship.PARENT_CHILD) {
            // Еще супруга добавили
            for (var pair : relationships.get(person1)) {
                if (!Objects.equals(pair.getLeft(), person2))
                    if (pair.getRight().equals(Relationship.SPOUSE)) {
                        relationships.computeIfAbsent(pair.getLeft(), k -> new HashSet<>())
                                .add(new Pair<>(person2, relationship));
                    }
            }
        }
        if (relationship == Relationship.SPOUSE) {
            relationships.computeIfAbsent(person2, k -> new HashSet<>())
                    .add(new Pair<>(person1, relationship));
        }
    }

    public List<String> getRelatives(String name) {
        List<String> relatives = new ArrayList<>();
        for (Pair<String, Relationship> pair : relationships.get(name)) {
            String relative = pair.getLeft();
            String relationStr = pair.getRight() == Relationship.SPOUSE ? "супруг(а)" : "сын/дочь";
            relatives.add(name + " -> " + relative + " - " + relationStr);
        }
        for (String key : relationships.keySet()) {
            for (Pair<String, Relationship> pair : relationships.get(key)) {
                if (pair.getLeft().equals(name)) {
                    String relative = pair.getLeft();
                    String relationStr = pair.getRight() == Relationship.SPOUSE ? "супруг(а)" : "сын/дочь";
                    String result = key + " -> " + relative + " - " + relationStr;
                    if (!relatives.contains(result)) {
                        relatives.add(key + " -> " + relative + " - " + relationStr);
                    }
                }
            }
        }
        return relatives;
    }

    public static void main(String[] args) {
        FamilyTree familyTree = new FamilyTree();
        try {
            File file = new File("C:\\Users\\a.petropavlovskiy\\IdeaProjects\\his-hw\\rels.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split("<->");
                String name1 = tokens[0].trim();
                String name2 = tokens[1].trim();
                familyTree.addRelationship(name1, name2, Relationship.SPOUSE);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
        }
        try {
            File file = new File("C:\\Users\\a.petropavlovskiy\\IdeaProjects\\his-hw\\rels-child.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tokens = line.split("->");
                String name1 = tokens[0].trim();
                String name2 = tokens[1].trim();
                familyTree.addRelationship(name1, name2, Relationship.PARENT_CHILD);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден.");
        }
//        familyTree.addRelationship("Коля", "Маша", Relationship.PARENT_CHILD);
//        familyTree.addRelationship("Коля", "Петя", Relationship.PARENT_CHILD);


        List<String> relatives = familyTree.getRelatives("Урбан");
        if (relatives != null) {
            for (String relative : relatives) {
                System.out.println(relative);
            }
        } else {
            System.out.println("smth wrong.");
        }
    }
}
