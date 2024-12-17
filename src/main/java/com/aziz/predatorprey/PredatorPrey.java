package com.aziz.predatorprey;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class Prey {
    int speed;
    int strength;
    int intelligence;
    String name;

    Prey(int speed, int strength, int intelligence, String name) {
        this.speed = speed;
        this.strength = strength;
        this.intelligence = intelligence;
        this.name = name;
    }

    double fitness() {
        // Example fitness function: weighted sum of characteristics
        return speed * 0.5 + strength * 0.3 + intelligence * 0.2;
    }

    void evolve() {
        this.speed += new Random().nextInt(3) + 1;
        this.strength += new Random().nextInt(2) + 1;
        this.intelligence += new Random().nextInt(2) + 1;
    }

    @Override
    public String toString() {
        return name + " (Prey) - Speed: " + speed + ", Strength: " + strength + ", Intelligence: " + intelligence;
    }
}

class Predator {
    int speed;
    int strength;
    int intelligence;
    String name;

    Predator(int speed, int strength, int intelligence, String name) {
        this.speed = speed;
        this.strength = strength;
        this.intelligence = intelligence;
        this.name = name;
    }

    double fitness() {
        // Example fitness function: weighted sum of characteristics
        return speed * 0.4 + strength * 0.4 + intelligence * 0.2;
    }

    void evolve() {
        this.speed += new Random().nextInt(3) + 1;
        this.strength += new Random().nextInt(2) + 1;
        this.intelligence += new Random().nextInt(2) + 1;
    }

    boolean capture(Prey prey) {
        return this.speed > prey.speed;
    }

    @Override
    public String toString() {
        return name + " (Predator) - Speed: " + speed + ", Strength: " + strength + ", Intelligence: " + intelligence;
    }
}

class CoEvolutionSimulation {
    ArrayList<Predator> predators;
    ArrayList<Prey> preys;
    Random rand = new Random();

    CoEvolutionSimulation() {
        predators = new ArrayList<>();
        preys = new ArrayList<>();
        initializePopulations();
    }

    void initializePopulations() {
        predators.clear();
        preys.clear();
        for (int i = 0; i < 5; i++) {
            predators.add(new Predator(rand.nextInt(20) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, "Predator " + (i + 1)));
            preys.add(new Prey(rand.nextInt(20) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, "Prey " + (i + 1)));
        }
    }

    void simulateGeneration() {
        for (int i = 0; i < predators.size(); i++) {
            Predator predator = predators.get(i);
            Prey prey = preys.get(i);
            if (predator.capture(prey)) {
                prey.evolve();
            } else {
                predator.evolve();
            }
        }
    }

    String displayPopulation(ArrayList<?> population) {
        StringBuilder sb = new StringBuilder();
        for (Object ind : population) {
            sb.append(ind.toString()).append("\n");
        }
        return sb.toString();
    }
}


public class PredatorPrey extends JFrame {
    private CoEvolutionSimulation simulation;
    private JTextArea predatorPopulationText;
    private JTextArea preyPopulationText;
    private int generation;
    private JPanel graphPanel;

    public PredatorPrey() {
        simulation = new CoEvolutionSimulation();
        generation = 0;

        setTitle("Predator & Prey Co-Evolution Simulation");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        predatorPopulationText = new JTextArea(8, 30);
        preyPopulationText = new JTextArea(8, 30);
        predatorPopulationText.setEditable(false);
        preyPopulationText.setEditable(false);

        JPanel populationPanel = new JPanel(new GridLayout(2, 1));
        populationPanel.add(new JScrollPane(predatorPopulationText));
        populationPanel.add(new JScrollPane(preyPopulationText));

        JButton evolveButton = new JButton("Simulate Generation");
        evolveButton.addActionListener(e -> {
            generation++;
            simulation.simulateGeneration();
            displayPopulations();
            updateGraph();
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            generation = 0;
            simulation.initializePopulations();
            displayPopulations();
            updateGraph();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(evolveButton);

        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(600, 400));

        add(new JLabel("Predator Population"), BorderLayout.NORTH);
        add(populationPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(graphPanel, BorderLayout.EAST);

        displayPopulations();
        updateGraph();
    }

    private void displayPopulations() {
        setTitle("Co-Evolution Simulation - Generation " + generation);
        predatorPopulationText.setText("Predators:\n" + simulation.displayPopulation(simulation.predators));
        preyPopulationText.setText("Preys:\n" + simulation.displayPopulation(simulation.preys));
    }

    private void updateGraph() {
        graphPanel.repaint();
    }

    private void drawGraph(Graphics g) {
        int width = graphPanel.getWidth();
        int height = graphPanel.getHeight();
        int barWidth = width / simulation.predators.size();

        // Draw bars for predators and preys based on fitness
        for (int i = 0; i < simulation.predators.size(); i++) {
            Predator predator = simulation.predators.get(i);
            Prey prey = simulation.preys.size() > i ? simulation.preys.get(i) : null;

            int predatorHeight = (int) (predator.fitness() * 10);
            int preyHeight = prey != null ? (int) (prey.fitness() * 10) : 0;

            // Draw predator bar
            g.setColor(Color.RED);
            g.fillRect(i * barWidth, height - predatorHeight, barWidth / 2, predatorHeight);

            // Draw prey bar
            if (prey != null) {
                g.setColor(Color.BLUE);
                g.fillRect(i * barWidth + barWidth / 2, height - preyHeight, barWidth / 2, preyHeight);
            }

            // Draw names
            g.setColor(Color.BLACK);
            g.drawString(predator.name, i * barWidth + 5, height - predatorHeight - 5);
            if (prey != null) {
                g.drawString(prey.name, i * barWidth + barWidth / 2 + 5, height - preyHeight - 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PredatorPrey frame = new PredatorPrey();
            frame.setVisible(true);
        });
    }
}
