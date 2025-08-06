package lorenzo.galacticcommandsystem.view;

import lorenzo.galacticcommandsystem.controller.MainController;
import lorenzo.galacticcommandsystem.model.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MainView extends JFrame {

    private final MainController controller;

    public MainView(MainController controller) {
        this.controller = controller;

        setTitle("Galactic Command System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#0B0F1A"));
        setLayout(new BorderLayout());

        showMainMenu();
    }

    private void showMainMenu() {
        getContentPane().removeAll();

        JPanel topPadding = new JPanel();
        topPadding.setOpaque(false);
        topPadding.setLayout(new BoxLayout(topPadding, BoxLayout.Y_AXIS));
        topPadding.add(Box.createVerticalStrut(40)); // Spazio sopra il titolo

        JLabel title = new JLabel("Galactic Command System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPadding.add(title);

        add(topPadding, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        String[] buttons = { "Spaceships", "Crewmates", "Missions", "Planets" };
        for (String label : buttons) {
            RoundedButton button = new RoundedButton(label);
            button.setFont(new Font("SansSerif", Font.BOLD, 20));
            Dimension buttonSize = new Dimension(450, 60);
            button.setPreferredSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setMinimumSize(buttonSize);            buttonPanel.add(button);
            buttonPanel.add(Box.createVerticalStrut(12));

            if (label.equals("Spaceships")) {
                button.addActionListener(e -> showSpaceShips());
            }
            if (label.equals("Missions")) {
                button.addActionListener(e -> showMissions());
            }
            if (label.equals("Planets")) {
                button.addActionListener(e -> showPlanets());
            }
            if (label.equals("Crewmates")) {
                button.addActionListener(e -> showCrewMembers());
            }
        }

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showSpaceShips() {
        List<SpaceShip> ships = controller.getAllSpaceShips();
        String[] columnNames = {"ID", "Name", "IsOperational"};
        String[][] data = new String[ships.size()][3];

        for (int i = 0; i < ships.size(); i++) {
            SpaceShip ship = ships.get(i);
            data[i][0] = "#" + String.format("%03d", ship.getId());
            data[i][1] = ship.getName();
            data[i][2] = String.valueOf(ship.isOperational());
        }

        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setOpaque(false);

        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20)); // Più spazio sopra

        // Arrow-style back button
        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

// Title label
        JLabel title = new JLabel("SPACE SHIPS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

// Combine back arrow and title in one panel
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(backArrow);
        leftPanel.add(title);

        topPanel.add(leftPanel, BorderLayout.WEST);

// Add new button on the right
        RoundedButton addButton = new RoundedButton("Add new");
        styleAddNewButton(addButton);
        addButton.addActionListener(e -> showCreateSpaceShipForm());
        topPanel.add(addButton, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(bottomPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String idStr = ((String) table.getValueAt(row, 0)).substring(1);
                        Long shipId = Long.parseLong(idStr);
                        showSpaceShipDetails(shipId);
                    }
                }
            }
        });

        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void showCreateSpaceShipForm() {
        getContentPane().removeAll();


        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 50)); // meno padding a sinistra

        JLabel title = new JLabel("Create Spaceship", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(30));

        JLabel nameLabel = new JLabel("Spaceship Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField();
        nameField.setBackground(new Color(40, 60, 90));
        nameField.setForeground(Color.WHITE);
        nameField.setCaretColor(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 50, 80)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedButton createButton = new RoundedButton("Create");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setBackground(new Color(30, 90, 220));
        createButton.setForeground(Color.WHITE);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(new Dimension(110, 30));
        createButton.setFocusPainted(false);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                controller.createNewSpaceShip(name);
                showSpaceShips();
            } else {
                JOptionPane.showMessageDialog(this, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(createButton);
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showSpaceShipDetails(Long shipId) {
        SpaceShip ship = controller.getSpaceShipById(shipId);

        if (ship == null) {
            JOptionPane.showMessageDialog(this, "Unable to load spaceship data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        getContentPane().removeAll();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSpaceShips();
            }
        });
        headerPanel.add(backArrow, BorderLayout.WEST);

        JLabel shipNameLabel = new JLabel(ship.getName(), SwingConstants.CENTER);
        shipNameLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        shipNameLabel.setForeground(Color.WHITE);
        shipNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        headerPanel.add(shipNameLabel, BorderLayout.CENTER);

        // CrewMates
        JLabel crewMatesLabel = new JLabel("CrewMates", SwingConstants.CENTER);
        crewMatesLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        crewMatesLabel.setForeground(Color.WHITE);
        crewMatesLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        crewMatesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Crew Table
        String[] crewColumns = {"Name", "Role"};
        List<CrewMember> crewList = controller.getCrewMembersByShipId(shipId);
        String[][] crewData = new String[crewList.size()][2];
        for (int i = 0; i < crewList.size(); i++) {
            CrewMember c = crewList.get(i);
            crewData[i][0] = c.getFullName();
            crewData[i][1] = c.getClass().getSimpleName();
        }

        JTable crewTable = new JTable(crewData, crewColumns) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        styleTable(crewTable);

        JScrollPane crewScrollPane = new JScrollPane(crewTable);
        crewScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        crewScrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        crewScrollPane.setOpaque(false);

        // Missions
        JLabel missionsLabel = new JLabel("Missions", SwingConstants.CENTER);
        missionsLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        missionsLabel.setForeground(Color.WHITE);
        missionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        missionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mission Table
        String[] missionColumns = {"Name", "Info"};
        List<Mission> missions = controller.getMissionsByShipId(shipId);
        String[][] missionData = new String[missions.size()][2];
        for (int i = 0; i < missions.size(); i++) {
            Mission m = missions.get(i);
            missionData[i][0] = m.getName();
            String info = (m.getActivationInfo() != null && !m.getActivationInfo().isEmpty())
                    ? m.getActivationInfo()
                    : m.getDeactivationInfo();
            missionData[i][1] = info != null ? info : "No Info";
        }

        JTable missionTable = new JTable(missionData, missionColumns) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        styleTable(missionTable);

        JScrollPane missionScrollPane = new JScrollPane(missionTable);
        missionScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        missionScrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        missionScrollPane.setOpaque(false);

        // Action Buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionButtonsPanel.setOpaque(false);
        String[] buttonLabels = {"Show History", "Dismantle", "Go to Mission", "Assign Crewmate"};
        for (String label : buttonLabels) {
            RoundedButton button = new RoundedButton(label);
            button.setPreferredSize(new Dimension(160, 45));
            button.setFont(new Font("SansSerif", Font.BOLD, 15));
            actionButtonsPanel.add(button);

            if ("Go to Mission".equals(label)) {
                button.addActionListener(e -> showMissionSelection(ship.getId()));
            }
            if ("Show History".equals(label)) {
                button.addActionListener(e -> showSpaceShipHistory(ship.getId()));
            }
        }

        // Main Layout
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(crewMatesLabel);
        mainPanel.add(crewScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(missionsLabel);
        mainPanel.add(missionScrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(actionButtonsPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        add(mainPanel, BorderLayout.CENTER);
        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void showMissionSelection(Long shipId) {
        getContentPane().removeAll();

        List<Mission> allMissions = controller.getUnassignedMissionsForShip(shipId);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#0B0F1A"));

        // Header panel with back arrow
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#0B0F1A"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSpaceShipDetails(shipId);
            }
        });
        headerPanel.add(backArrow, BorderLayout.WEST);

        JLabel title = new JLabel("Assign Mission");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(title, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Left content panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.decode("#0B0F1A"));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mission list
        ButtonGroup buttonGroup = new ButtonGroup();
        Map<JRadioButton, Mission> radioMissionMap = new HashMap<>();

        JPanel missionListPanel = new JPanel();
        missionListPanel.setLayout(new BoxLayout(missionListPanel, BoxLayout.Y_AXIS));
        missionListPanel.setOpaque(false);

        for (Mission mission : allMissions) {
            JPanel missionPanel = new JPanel();
            missionPanel.setLayout(new BoxLayout(missionPanel, BoxLayout.X_AXIS));
            missionPanel.setOpaque(false);
            missionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(mission.getName());
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            infoPanel.add(nameLabel);

            JLabel statusLabel = new JLabel(mission.getMissionState().name());
            statusLabel.setForeground(Color.LIGHT_GRAY);
            statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            infoPanel.add(statusLabel);

            String infoText = mission.getMissionState() == MissionState.ACTIVE
                    ? "Info: " + (mission.getActivationInfo() != null ? mission.getActivationInfo() : "N/A")
                    : "Info: " + (mission.getDeactivationInfo() != null ? mission.getDeactivationInfo() : "N/A");

            JLabel infoLabel = new JLabel(infoText);
            infoLabel.setForeground(Color.GRAY);
            infoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            infoPanel.add(infoLabel);

            StyledRadioButton radioButton = new StyledRadioButton("");
            radioButton.setOpaque(false);
            buttonGroup.add(radioButton);
            radioMissionMap.put(radioButton, mission);

            missionPanel.add(infoPanel);
            missionPanel.add(Box.createHorizontalStrut(10));
            missionPanel.add(radioButton);

            missionListPanel.add(missionPanel);
        }

        JScrollPane scrollPane = new JScrollPane(missionListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(scrollPane);

        leftPanel.add(Box.createVerticalStrut(10));

        JLabel addInfoLabel = new JLabel("Add info:");
        addInfoLabel.setForeground(Color.WHITE);
        addInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(addInfoLabel);

        JTextField activationField = new JTextField();
        activationField.setBackground(new Color(40, 60, 90));
        activationField.setForeground(Color.WHITE);
        activationField.setCaretColor(Color.WHITE);
        activationField.setMaximumSize(new Dimension(400, 30));
        leftPanel.add(activationField);

        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // Right-side ADD button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.decode("#0B0F1A"));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(Box.createVerticalGlue());

        RoundedButton addButton = new RoundedButton("ADD");
        addButton.setPreferredSize(new Dimension(80, 40));
        addButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonPanel.add(addButton);

        mainPanel.add(buttonPanel, BorderLayout.EAST);

        addButton.addActionListener(e -> {
            Mission selectedMission = radioMissionMap.entrySet().stream()
                    .filter(entry -> entry.getKey().isSelected())
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (selectedMission == null) {
                JOptionPane.showMessageDialog(this, "Please select a mission.");
                return;
            }

            String info = activationField.getText().trim();
            if (info.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter activation info.");
                return;
            }

            controller.assignMissionToShip(selectedMission.getId(), shipId, info);
            JOptionPane.showMessageDialog(this, "Mission assigned.");
            showMissionPage(selectedMission.getId());
        });

        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMissionPage(Long missionId) {
        getContentPane().removeAll();

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.decode("#0B0F1A"));

        // === Header with Back Arrow and Title ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#0B0F1A"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel homeIcon = new JLabel("⌂");
        homeIcon.setFont(new Font("SansSerif", Font.PLAIN, 28));
        homeIcon.setForeground(Color.WHITE);
        homeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        homeIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        JLabel headerTitle = new JLabel("Mission Overview");
        headerTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(homeIcon, BorderLayout.WEST);
        headerPanel.add(headerTitle, BorderLayout.CENTER);

        wrapperPanel.add(headerPanel, BorderLayout.NORTH);

        // === Main Panel with GridLayout ===
        JPanel mainPanel = new JPanel(new GridLayout(1, 3));
        mainPanel.setBackground(Color.decode("#0B0F1A"));

        Font titleFont = new Font("SansSerif", Font.BOLD, 28);
        Font textFont = new Font("SansSerif", Font.PLAIN, 16);
        Color textColor = Color.WHITE;
        Color softDarkBlue = new Color(59, 89, 152);

        // === Left Column: Mission Details ===
        JPanel missionDetailsPanel = new JPanel();
        missionDetailsPanel.setLayout(new BoxLayout(missionDetailsPanel, BoxLayout.Y_AXIS));
        missionDetailsPanel.setBackground(Color.decode("#0B0F1A"));
        missionDetailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel missionTitle = new JLabel("Mission Details", SwingConstants.CENTER);
        missionTitle.setFont(titleFont);
        missionTitle.setForeground(textColor);
        missionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        Mission mission = controller.getMissionById(missionId);

        JLabel nameLabel = new JLabel("Name: " + (mission != null ? mission.getName() : "Unknown"));
        nameLabel.setForeground(textColor);
        nameLabel.setFont(textFont);

        JLabel statusLabel = new JLabel("Status: " + (mission != null ? mission.getMissionState() : "Unknown"));
        statusLabel.setForeground(textColor);
        statusLabel.setFont(textFont);

        missionDetailsPanel.add(missionTitle);
        missionDetailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        missionDetailsPanel.add(nameLabel);
        missionDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        missionDetailsPanel.add(statusLabel);

        // === Middle Column: Mission Objectives ===
        JPanel objectivesPanel = new JPanel();
        objectivesPanel.setLayout(new BoxLayout(objectivesPanel, BoxLayout.Y_AXIS));
        objectivesPanel.setBackground(Color.decode("#0B0F1A"));
        objectivesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel objectivesTitle = new JLabel("Objectives", SwingConstants.CENTER);
        objectivesTitle.setFont(titleFont);
        objectivesTitle.setForeground(textColor);
        objectivesTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        objectivesPanel.add(objectivesTitle);
        objectivesPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        List<Objective> objectives = controller.getObjectivesByMissionId(missionId);
        for (Objective obj : objectives) {
            addObjective(objectivesPanel, obj.getTitle(), obj.getDescription(), obj.getTypes());
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#0B0F1A"));

        JButton addButton = createStyledButton("+", softDarkBlue);
        JButton removeButton = createStyledButton("-", softDarkBlue);

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(removeButton);

        objectivesPanel.add(Box.createVerticalStrut(10));
        objectivesPanel.add(buttonPanel);

        // === Right Column: Assigned Spaceships ===
        JPanel spaceshipPanel = new JPanel();
        spaceshipPanel.setLayout(new BoxLayout(spaceshipPanel, BoxLayout.Y_AXIS));
        spaceshipPanel.setBackground(Color.decode("#0B0F1A"));
        spaceshipPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel spaceshipTitle = new JLabel("Spaceships", SwingConstants.CENTER);
        spaceshipTitle.setFont(titleFont);
        spaceshipTitle.setForeground(textColor);
        spaceshipTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        spaceshipPanel.add(spaceshipTitle);
        spaceshipPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        List<String> shipNames = controller.getSpaceShipNamesByMissionId(missionId);
        for (String shipName : shipNames) {
            addSpaceship(spaceshipPanel, shipName); // Optional: add date info
        }

        // Add all sections
        mainPanel.add(missionDetailsPanel);
        mainPanel.add(objectivesPanel);
        mainPanel.add(spaceshipPanel);

        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showMissions() {
        List<Mission> missions = controller.getAllMissions(); // Assuming this method exists
        String[] columnNames = {"ID", "Name", "Funding", "State"};
        String[][] data = new String[missions.size()][4];


        for (int i = 0; i < missions.size(); i++) {
            Mission mission = missions.get(i);
            data[i][0] = "#" + String.format("%03d", mission.getId());
            data[i][1] = mission.getName();
            data[i][2] = String.format("$%.2f", mission.getFunding());
            data[i][3] = mission.getMissionState().toString();
        }

        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setOpaque(false);

        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        JLabel title = new JLabel("MISSIONS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(backArrow);
        leftPanel.add(title);

        topPanel.add(leftPanel, BorderLayout.WEST);

        RoundedButton addButton = new RoundedButton("Add new");
        styleAddNewButton(addButton);
        addButton.addActionListener(e -> showCreateMissionForm());
        topPanel.add(addButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(bottomPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String idStr = ((String) table.getValueAt(row, 0)).substring(1);
                        Long missionId = Long.parseLong(idStr);
                        showMissionPage(missionId);
                    }
                }
            }
        });

        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void showCreateMissionForm() {
        getContentPane().removeAll();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 50));

        JLabel title = new JLabel("Create Mission", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(30));

        // ---- Mission Name ----
        JLabel nameLabel = new JLabel("Mission Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField();
        styleTextField(nameField);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(20));

        // ---- Funding ----
        JLabel fundingLabel = new JLabel("Funding (min 100):");
        fundingLabel.setForeground(Color.WHITE);
        fundingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fundingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(fundingLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField fundingField = new JTextField();
        styleTextField(fundingField);
        formPanel.add(fundingField);
        formPanel.add(Box.createVerticalStrut(20));

        // ---- Info ----
        JLabel deactivationLabel = new JLabel("Info:");
        deactivationLabel.setForeground(Color.WHITE);
        deactivationLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        deactivationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(deactivationLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField deactivationField = new JTextField();
        styleTextField(deactivationField);
        formPanel.add(deactivationField);
        formPanel.add(Box.createVerticalStrut(30));

        // ---- Button Panel ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedButton createButton = new RoundedButton("Create");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setBackground(new Color(30, 90, 220));
        createButton.setForeground(Color.WHITE);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(new Dimension(110, 30));
        createButton.setFocusPainted(false);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String fundingStr = fundingField.getText().trim();
            String deactivationInfo = deactivationField.getText().trim();

            if (name.isEmpty() || fundingStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Funding are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double funding = Double.parseDouble(fundingStr);
                if (funding < 100) {
                    JOptionPane.showMessageDialog(this, "Funding must be at least 100", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Mission mission = new Mission(name, funding);
                if (!deactivationInfo.isEmpty()) {
                    mission.setInactiveStatus(deactivationInfo);
                }
                controller.createMission(mission);
                showMissions();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Funding must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(createButton);
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showPlanets() {
        List<Planet> planets = controller.getAllPlanets(); // Your controller method
        String[] columnNames = {"ID", "Name", "Atmosphere", "Position"};
        String[][] data = new String[planets.size()][4];


        for (int i = 0; i < planets.size(); i++) {
            Planet planet = planets.get(i);
            data[i][0] = "#" + String.format("%03d", planet.getId());
            data[i][1] = planet.getName();
            data[i][2] = planet.getAtmosphereType();
            data[i][3] = planet.getPosition();
        }

        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setOpaque(false);

        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        JLabel title = new JLabel("PLANETS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(backArrow);
        leftPanel.add(title);

        topPanel.add(leftPanel, BorderLayout.WEST);

        RoundedButton addButton = new RoundedButton("Add new");
        styleAddNewButton(addButton);
        addButton.addActionListener(e -> showCreatePlanetForm());
        topPanel.add(addButton, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(bottomPanel, BorderLayout.SOUTH);

        /*
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String idStr = ((String) table.getValueAt(row, 0)).substring(1);
                        Long planetId = Long.parseLong(idStr);
                        showPlanetDetails(planetId);
                    }
                }
            }
        });

         */

        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void showCreatePlanetForm() {
        getContentPane().removeAll();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 50)); // Less left padding

        JLabel title = new JLabel("Create Planet", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(30));

        // ---- Name ----
        JLabel nameLabel = new JLabel("Planet Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField();
        styleTextField(nameField);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(20));

        // ---- Atmosphere Type ----
        JLabel atmosphereLabel = new JLabel("Atmosphere Type:");
        atmosphereLabel.setForeground(Color.WHITE);
        atmosphereLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        atmosphereLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(atmosphereLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField atmosphereField = new JTextField();
        styleTextField(atmosphereField);
        formPanel.add(atmosphereField);
        formPanel.add(Box.createVerticalStrut(20));

        // ---- Position ----
        JLabel positionLabel = new JLabel("Position (x,y,z):");
        positionLabel.setForeground(Color.WHITE);
        positionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        positionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(positionLabel);
        formPanel.add(Box.createVerticalStrut(8));

        JTextField positionField = new JTextField();
        styleTextField(positionField);
        formPanel.add(positionField);
        formPanel.add(Box.createVerticalStrut(30));

        // ---- Button Panel ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        RoundedButton createButton = new RoundedButton("Create");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setBackground(new Color(30, 90, 220));
        createButton.setForeground(Color.WHITE);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createButton.setPreferredSize(new Dimension(110, 30));
        createButton.setFocusPainted(false);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String atmosphere = atmosphereField.getText().trim();
            String position = positionField.getText().trim();

            if (name.isEmpty() || atmosphere.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Planet newPlanet = Planet.builder()
                    .name(name)
                    .atmosphereType(atmosphere)
                    .position(position)
                    .build();

            controller.createPlanet(newPlanet);
            showPlanets();
        });

        buttonPanel.add(createButton);
        formPanel.add(buttonPanel);

        add(formPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showCrewMembers() {
        List<CrewMember> crewMembers = controller.getAllCrewMembers();
        String[] columnNames = {"ID", "Full Name", "Credits", "SpaceShip", "Planet"};
        String[][] data = new String[crewMembers.size()][5];

        for (int i = 0; i < crewMembers.size(); i++) {
            CrewMember cm = crewMembers.get(i);
            data[i][0] = "#" + String.format("%03d", cm.getId());
            data[i][1] = cm.getFullName();
            data[i][2] = String.format("%.2f", cm.getCredits());
            data[i][3] = cm.getSpaceShip() != null ? cm.getSpaceShip().getName() : "N/A";
            data[i][4] = cm.getPlanet() != null ? cm.getPlanet().getName() : "N/A";
        }

        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setOpaque(false);

        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showMainMenu();
            }
        });

        JLabel title = new JLabel("CREW MEMBERS");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(backArrow);
        leftPanel.add(title);

        topPanel.add(leftPanel, BorderLayout.WEST);

        /*
        RoundedButton addButton = new RoundedButton("Add new");
        styleAddNewButton(addButton);
        addButton.addActionListener(e -> showCreateCrewMemberForm()); // Da implementare
        topPanel.add(addButton, BorderLayout.EAST);

         */

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(bottomPanel, BorderLayout.SOUTH);

        /*
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String idStr = ((String) table.getValueAt(row, 0)).substring(1);
                        Long id = Long.parseLong(idStr);
                        showCrewMemberDetails(id); // Da implementare se vuoi
                    }
                }
            }
        });

         */

        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void showSpaceShipHistory(Long spaceshipId) {
        List<CrewHistory> historyRecords = controller.getSpaceShipHistory(spaceshipId);
        SpaceShip spaceShip = controller.getSpaceShipById(spaceshipId);

        String[] columnNames = {"ID", "Assignment Date", "Crew Member ID", "Space Ship ID"};
        String[][] data = new String[historyRecords.size()][4];

        for (int i = 0; i < historyRecords.size(); i++) {
            CrewHistory history = historyRecords.get(i);

            data[i][0] = "#" + String.format("%03d", history.getId());
            data[i][1] = history.getAssignmentDate() != null ? history.getAssignmentDate().toString() : "N/A";
            data[i][2] = "#" + String.format("%03d", history.getCrewMember().getId());
            data[i][3] = "#" + String.format("%03d", history.getSpaceShip().getId());
        }

        JTable table = new JTable(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        scrollPane.getViewport().setBackground(Color.decode("#0B0F1A"));
        scrollPane.setOpaque(false);

        getContentPane().removeAll();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));

        JLabel backArrow = new JLabel("←");
        backArrow.setFont(new Font("SansSerif", Font.PLAIN, 28));
        backArrow.setForeground(Color.WHITE);
        backArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backArrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        backArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSpaceShipDetails(spaceshipId);
            }
        });

        String spaceShipName = spaceShip != null ? spaceShip.getName() : "Unknown";
        JLabel title = new JLabel("SPACESHIP HISTORY - " + spaceShipName);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(backArrow);
        leftPanel.add(title);

        topPanel.add(leftPanel, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add summary information
        JLabel summaryLabel = new JLabel("Total Assignments: " + historyRecords.size());
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        summaryLabel.setForeground(Color.LIGHT_GRAY);
        bottomPanel.add(summaryLabel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(Color.decode("#0B0F1A"));
        revalidate();
        repaint();
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(40, 60, 90));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 50, 80)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setMaximumSize(new Dimension(300, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(50, 40));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(background, 1, true));
        return button;
    }

    private void addObjective(JPanel panel, String title, String description, Set<ObjectiveType> types) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.LIGHT_GRAY);

        // Concatena i nomi degli enum separati da virgole
        String typeText = types.stream()
                .map(ObjectiveType::name) // o .toString() se vuoi usare un override personalizzato
                .collect(Collectors.joining(", "));

        JLabel typeLabel = new JLabel(typeText);
        typeLabel.setForeground(Color.GRAY);

        panel.add(titleLabel);
        panel.add(descLabel);
        panel.add(typeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addSpaceship(JPanel panel, String name) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        JLabel dateLabel = new JLabel("");
        dateLabel.setForeground(Color.LIGHT_GRAY);

        panel.add(nameLabel);
        panel.add(dateLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setBackground(Color.decode("#0E1117"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.decode("#1F2937"));
        table.setSelectionBackground(Color.decode("#1F2937"));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.decode("#1F2937"));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);
    }

    static class RoundedButton extends JButton {
        private final int radius = 25;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new RoundedBorder(radius));
            setBackground(Color.decode("#A0C4FF"));
            setForeground(Color.BLACK);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.decode("#A0C4FF"));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    private void styleAddNewButton(JButton button) {
        button.setBackground(new Color(30, 30, 30)); // stesso colore del header
        button.setForeground(Color.WHITE);           // testo bianco
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    static class RoundedBorder implements Border {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.decode("#A0C4FF"));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    static class StyledRadioButton extends JRadioButton {
        public StyledRadioButton(String text) {
            super(text);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setIcon(new CustomRadioIcon());
            setSelectedIcon(new CustomRadioIcon(true));
        }

        private static class CustomRadioIcon implements Icon {
            private final boolean selected;

            public CustomRadioIcon() {
                this(false);
            }

            public CustomRadioIcon(boolean selected) {
                this.selected = selected;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getIconWidth();
                Color outer = new Color(70, 90, 120);
                Color inner = new Color(180, 200, 240);

                // Sfondo del bottone
                g2.setColor(new Color(20, 25, 35));
                g2.fillRoundRect(x, y, size, size, 6, 6);

                // Bordo chiaro
                g2.setColor(outer);
                g2.drawRoundRect(x, y, size - 1, size - 1, 6, 6);

                if (selected) {
                    g2.setColor(inner);
                    g2.fillRoundRect(x + 4, y + 4, size - 8, size - 8, 4, 4);
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 18;
            }

            @Override
            public int getIconHeight() {
                return 18;
            }
        }
    }
}