
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class AutoSyncApp extends JFrame {


    // =============== DESIGN SYSTEM ===============

    // Charcoal, warm ivory and gold palette inspired by the supplied reference.
    private final Color BG = new Color(243, 235, 216);
    private final Color SIDEBAR = new Color(31, 37, 41);
    private final Color PANEL = new Color(249, 243, 229);
    private final Color PANEL2 = new Color(226, 211, 174);
    private final Color BORDER = new Color(218, 205, 177);

    private final Color WHITE = new Color(39, 49, 56);       // Main dark text
    private final Color MUTED = new Color(112, 116, 116);    // Secondary text

    private final Color TEAL = new Color(31, 37, 41);        // Charcoal
    private final Color BLUE = new Color(91, 111, 124);      // Steel blue
    private final Color ORANGE = new Color(190, 148, 52);    // Gold
    private final Color RED = new Color(166, 54, 48);        // Alert red
    private final Color GREEN = new Color(55, 126, 96);      // Status green


    private final Path dataFolder = Paths.get("autosync_data");
    private final JPanel content = new JPanel(new BorderLayout());
    private final JPanel sidebar = new JPanel();
    private final JLabel pageTitle = new JLabel();
    private final JLabel clock = new JLabel();
    private final Map<String, JButton> nav = new LinkedHashMap<>();

    private String currentPage = "Dashboard";

    private final String[][] modules = {
            {"Customers", "customers.txt",
                    "Customer ID", "Name", "Phone", "Email"},

            {"Vehicles", "vehicles.txt",
                    "Vehicle ID", "Registration", "Make / Model",
                    "Year", "Mileage", "Customer ID"},

            {"Mechanics", "mechanics.txt",
                    "Mechanic ID", "Name", "Specialization", "Status"},

            {"Spare Parts", "parts.txt",
                    "Part ID", "Part Name", "Quantity", "Unit Price"},

            {"Service Jobs", "jobs.txt",
                    "Job ID", "Vehicle ID", "Service Type",
                    "Mechanic ID", "Status", "Labor Cost"},

            {"Invoices", "invoices.txt",
                    "Invoice ID", "Job ID", "Customer ID",
                    "Labor", "Parts", "Discount", "Total"},

            {"Maintenance", "maintenance.txt",
                    "Vehicle ID", "Recommendation", "Due Mileage", "Status"}
    };

    public AutoSyncApp() {
        setTitle("AutoSync | Workshop Command Center");
        setSize(1450, 900);
        setMinimumSize(new Dimension(1050, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        buildSidebar();

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(BG);
        workspace.add(buildTopbar(), BorderLayout.NORTH);

        content.setBackground(BG);
        workspace.add(content, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(workspace, BorderLayout.CENTER);

        navigate("Dashboard");
    }

    // =============== SIDEBAR ===============

    private void buildSidebar() {
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(225, 0));

        JPanel brand = new JPanel();
        brand.setBackground(SIDEBAR);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(26, 20, 26, 10));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandRow.add(new BrandMark(34, 34));

        JLabel logo = new JLabel("AutoSync");
        logo.setForeground(new Color(249, 243, 229));
        logo.setFont(new Font("SansSerif", Font.BOLD, 23));
        brandRow.add(logo);

        JLabel sub = new JLabel("WORKSHOP INTELLIGENCE");
        sub.setForeground(new Color(190, 148, 52));
        sub.setFont(new Font("SansSerif", Font.BOLD, 9));
        sub.setBorder(new EmptyBorder(0, 4, 0, 0));

        brand.add(brandRow);
        brand.add(Box.createVerticalStrut(10));
        brand.add(sub);

        sidebar.add(brand, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setBackground(SIDEBAR);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(0, 11, 10, 11));

        addSection(menu, "COMMAND CENTER");
        addNav(menu, "Dashboard", "▦");

        addSection(menu, "WORKSHOP");
        addNav(menu, "Customers", "♙");
        addNav(menu, "Vehicles", "▣");
        addNav(menu, "Mechanics", "⚙");
        addNav(menu, "Service Jobs", "▤");

        addSection(menu, "OPERATIONS");
        addNav(menu, "Spare Parts", "▧");
        addNav(menu, "Invoices", "Rs.");
        addNav(menu, "Maintenance", "◷");

        sidebar.add(menu, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(SIDEBAR);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(new EmptyBorder(15, 19, 22, 10));

        JLabel online = new JLabel("●  SYSTEM OPERATIONAL");
        online.setForeground(new Color(243, 235, 216));
        online.setFont(new Font("SansSerif", Font.BOLD, 11));
        online.setOpaque(true);
        online.setBackground(new Color(31, 37, 41));
        online.setBorder(new EmptyBorder(8, 7, 8, 7));
        online.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel version = new JLabel("AutoSync Desktop  •  v1.0");
        version.setForeground(MUTED);
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));

        footer.add(online);
        footer.add(Box.createVerticalStrut(9));
        footer.add(version);

        sidebar.add(footer, BorderLayout.SOUTH);
    }

    private void addSection(JPanel menu, String title) {
        JLabel label = new JLabel(title);
        label.setForeground(new Color(116, 116, 108));
        label.setFont(new Font("SansSerif", Font.BOLD, 9));
        label.setBorder(new EmptyBorder(19, 12, 9, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(label);
    }

    private void addNav(JPanel menu, String name, String symbol) {
        JButton button = new JButton("  " + symbol + "    " + name);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(200, 42));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));

        button.setForeground(new Color(249, 243, 229)); // Warm ivory text
        button.setBackground(SIDEBAR);                  // Charcoal background
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(0, 12, 0, 5));
        button.setFocusPainted(false);

        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

        button.addActionListener(e -> navigate(name));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!name.equals(currentPage)) {
                    button.setBackground(new Color(49, 56, 60));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!name.equals(currentPage)) {
                    button.setBackground(SIDEBAR);
                }
            }
        });

        nav.put(name, button);
        menu.add(button);
        menu.add(Box.createVerticalStrut(3));
    }

    private void updateNav(String page) {
        currentPage = page;

        for (Map.Entry<String, JButton> entry : nav.entrySet()) {
            boolean selected = entry.getKey().equals(page);
            JButton b = entry.getValue();

            b.setOpaque(true);
            b.setContentAreaFilled(true);

            b.setBackground(selected
                    ? new Color(190, 148, 52)  // Gold selected button
                    : SIDEBAR);               // Charcoal unselected buttons

            b.setForeground(selected
                    ? new Color(31, 37, 41)   // Dark text on gold
                    : new Color(249, 243, 229)); // Ivory text on charcoal

            if (selected) {
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 4, 0, 0, new Color(249, 243, 229)),
                        new EmptyBorder(0, 9, 0, 5)));
            } else {
                b.setBorder(new EmptyBorder(0, 12, 0, 5));
            }
        }
    }

    // =============== TOP BAR ===============

    private JPanel buildTopbar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(SIDEBAR);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(190, 148, 52)),
                new EmptyBorder(10, 22, 10, 22)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 11, 0));
        left.setOpaque(false);
        left.add(new BrandMark(34, 34));

        JLabel brandName = new JLabel("AutoSync");
        brandName.setForeground(new Color(249, 243, 229));
        brandName.setFont(new Font("SansSerif", Font.BOLD, 22));
        left.add(brandName);

        JLabel separator = new JLabel("│");
        separator.setForeground(new Color(190, 148, 52));
        left.add(separator);

        JLabel workspaceLabel = new JLabel("Workshop Command Center");
        workspaceLabel.setForeground(new Color(211, 211, 205));
        workspaceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        left.add(workspaceLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 2));
        right.setOpaque(false);

        JLabel status = new JLabel("●  ONLINE");
        status.setForeground(new Color(77, 170, 125));
        status.setFont(new Font("SansSerif", Font.BOLD, 10));

        clock.setForeground(new Color(224, 222, 214));
        clock.setFont(new Font("Monospaced", Font.PLAIN, 11));
        updateClock();

        JButton refresh = button("↻  Refresh", false);
        refresh.addActionListener(e -> navigate(currentPage));

        JButton logout = button("⇥  Log Out", false);
        logout.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to log out?",
                    "AutoSync Logout", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginDialog login = new LoginDialog();
                    if (login.showLogin()) {
                        new AutoSyncApp().setVisible(true);
                    } else {
                        System.exit(0);
                    }
                });
            }
        });

        right.add(status);
        right.add(clock);
        right.add(refresh);
        right.add(logout);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private void updateClock() {
        clock.setText(new java.text.SimpleDateFormat(
                "dd MMM yyyy  HH:mm").format(new Date()));
    }

    private void navigate(String page) {
        updateNav(page);
        pageTitle.setText(page.toUpperCase(Locale.ROOT));

        if (page.equals("Dashboard")) {
            showDashboard();
            return;
        }

        if (page.equals("Spare Parts")) {
            showSpareParts();
            return;
        }

        for (String[] m : modules) {
            if (m[0].equals(page)) {
                showModule(m);
                return;
            }
        }
    }

    private void setContent(JPanel panel) {
        content.removeAll();
        content.add(panel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
        updateClock();
    }

    // =============== DASHBOARD ===============

    private void showDashboard() {
        JPanel root = new JPanel(new BorderLayout(0, 19));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(23, 25, 20, 25));

        JPanel intro = new JPanel(new BorderLayout());
        intro.setOpaque(false);

        JPanel introText = new JPanel();
        introText.setOpaque(false);
        introText.setLayout(new BoxLayout(introText, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Workshop command center");
        heading.setFont(new Font("SansSerif", Font.BOLD, 25));
        heading.setForeground(WHITE);

        JLabel description = new JLabel(
                "Monitor service operations, workshop capacity and vehicle care.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 12));
        description.setForeground(MUTED);

        introText.add(heading);
        introText.add(Box.createVerticalStrut(7));
        introText.add(description);

        JButton newJob = button("+  New Service Job", true);
        newJob.addActionListener(e -> navigate("Service Jobs"));

        intro.add(introText, BorderLayout.WEST);
        intro.add(newJob, BorderLayout.EAST);
        root.add(intro, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        int customerCount = count("customers.txt", 4);
        int vehicleCount = count("vehicles.txt", 6);
        int mechanicCount = count("mechanics.txt", 4);
        int jobCount = 0;
        int activeCount = 0;
        int lowStock = 0;
        int completed = 0;
        double revenue = 0;

        try {
            for (String[] r : read("jobs.txt", 6)) {
                jobCount++;
                if (r[4].equals("Scheduled") ||
                        r[4].equals("In Progress")) activeCount++;
                if (r[4].equals("Completed")) completed++;
            }

            for (String[] r : read("parts.txt", 4)) {
                if (isLowStock(r)) lowStock++;
            }

            for (String[] r : read("invoices.txt", 7)) {
                revenue += Double.parseDouble(r[6]);
            }
        } catch (Exception ignored) {}

        JPanel metrics = new JPanel(new GridLayout(1, 4, 13, 0));
        metrics.setOpaque(false);

        metrics.add(metric("CUSTOMERS", String.valueOf(customerCount),
                "Registered clients", "👤", TEAL));
        metrics.add(metric("VEHICLES", String.valueOf(vehicleCount),
                "Registered vehicles", "🚘", BLUE));
        metrics.add(metric("ACTIVE JOBS", String.valueOf(activeCount),
                "Scheduled + in progress", "🔧", ORANGE));
        metrics.add(metric("INVOICE REVENUE", money(revenue),
                "Recorded invoice totals", "₨", GREEN));

        body.add(metrics, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 15, 0));
        lower.setOpaque(false);

        // Service schedule
        JPanel schedule = cardPanel();
        schedule.setLayout(new BorderLayout(0, 12));
        schedule.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel scheduleHeader = new JPanel(new BorderLayout());
        scheduleHeader.setOpaque(false);

        JLabel scheduleTitle = title("Workshop service schedule");
        JLabel live = new JLabel("  ● LIVE RECORDS  ");
        live.setOpaque(true);
        live.setBackground(new Color(31, 37, 41));
        live.setForeground(new Color(220, 207, 177));
        live.setFont(new Font("SansSerif", Font.BOLD, 9));

        scheduleHeader.add(scheduleTitle, BorderLayout.WEST);
        scheduleHeader.add(live, BorderLayout.EAST);
        schedule.add(scheduleHeader, BorderLayout.NORTH);

        String[] columns = {
                "JOB ID", "VEHICLE", "SERVICE", "MECHANIC", "STATUS"
        };

        DefaultTableModel scheduleModel = new DefaultTableModel(
                columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        try {
            List<String[]> jobs = read("jobs.txt", 6);
            for (int i = jobs.size() - 1;
                 i >= Math.max(0, jobs.size() - 8); i--) {
                String[] r = jobs.get(i);
                scheduleModel.addRow(new Object[] {
                        r[0], r[1], r[2], r[3], r[4]
                });
            }
        } catch (Exception ignored) {}

        JTable table = createTable(scheduleModel);
        schedule.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton openJobs = button("View All Service Jobs  →", true);
        openJobs.addActionListener(e -> navigate("Service Jobs"));

        JPanel scheduleFooter = new JPanel(new BorderLayout());
        scheduleFooter.setOpaque(false);
        scheduleFooter.add(openJobs, BorderLayout.WEST);

        JLabel jobsTotal = new JLabel(jobCount + " total jobs");
        jobsTotal.setForeground(MUTED);
        jobsTotal.setFont(new Font("SansSerif", Font.PLAIN, 10));
        scheduleFooter.add(jobsTotal, BorderLayout.EAST);

        schedule.add(scheduleFooter, BorderLayout.SOUTH);

        // Operational intelligence
        JPanel operations = cardPanel();
        operations.setLayout(new BorderLayout(0, 14));
        operations.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel operationsHeader = new JPanel(new BorderLayout());
        operationsHeader.setOpaque(false);
        operationsHeader.add(title("Operational intelligence"),
                BorderLayout.WEST);

        JLabel today = new JLabel("WORKSHOP HEALTH");
        today.setForeground(MUTED);
        today.setFont(new Font("SansSerif", Font.BOLD, 9));
        operationsHeader.add(today, BorderLayout.EAST);
        operations.add(operationsHeader, BorderLayout.NORTH);

        JPanel alerts = new JPanel();
        alerts.setOpaque(false);
        alerts.setLayout(new BoxLayout(alerts, BoxLayout.Y_AXIS));

        alerts.add(alert("PARTS INVENTORY",
                lowStock == 0
                        ? "No parts at or below the low-stock threshold."
                        : lowStock + " part records require stock review.",
                lowStock == 0 ? GREEN : ORANGE));

        alerts.add(Box.createVerticalStrut(10));

        alerts.add(alert("WORKSHOP CAPACITY",
                mechanicCount + " mechanic record(s) registered.",
                BLUE));

        alerts.add(Box.createVerticalStrut(10));

        alerts.add(alert("SERVICE COMPLETION",
                completed + " job(s) marked as completed.",
                GREEN));

        alerts.add(Box.createVerticalStrut(10));

        alerts.add(alert("MAINTENANCE REGISTER",
                count("maintenance.txt", 4)
                        + " maintenance reminder record(s).",
                TEAL));

        alerts.add(Box.createVerticalGlue());
        operations.add(alerts, BorderLayout.CENTER);

        JPanel quickActions = new JPanel(new GridLayout(1, 3, 8, 0));
        quickActions.setOpaque(false);


        JButton vehicles = button("Manage Vehicles", false);
        JButton mechanics = button("Manage Mechanics", false);
        JButton parts = button("Manage Spare Parts", false);

        vehicles.addActionListener(e -> navigate("Vehicles"));
        mechanics.addActionListener(e -> navigate("Mechanics"));
        parts.addActionListener(e -> navigate("Spare Parts"));

        quickActions.add(vehicles);
        quickActions.add(mechanics);
        quickActions.add(parts);

        operations.add(quickActions, BorderLayout.SOUTH);

        lower.add(schedule);
        lower.add(operations);

        body.add(lower, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel brandLine = new JLabel(
                "AUTOSYNC  /  WORKSHOP MANAGEMENT PLATFORM");
        brandLine.setForeground(new Color(116, 116, 108));
        brandLine.setFont(new Font("SansSerif", Font.BOLD, 9));

        JLabel storage = new JLabel("LOCAL DATA STORAGE");
        storage.setForeground(new Color(116, 116, 108));
        storage.setFont(new Font("SansSerif", Font.PLAIN, 9));

        footer.add(brandLine, BorderLayout.WEST);
        footer.add(storage, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        setContent(root);
    }

    private JPanel metric(String heading, String value,
                          String detail, String symbol, Color accent) {
        JPanel p = cardPanel();
        p.setLayout(new BorderLayout(0, 12));
        p.setBorder(new EmptyBorder(16, 16, 15, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel label = new JLabel(heading);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel icon = new JLabel(symbol, SwingConstants.CENTER);
        icon.setPreferredSize(new Dimension(40, 40));
        icon.setOpaque(true);
        icon.setBackground(PANEL2);
        icon.setForeground(accent);
        icon.setFont(new Font("SansSerif", Font.BOLD, 21));

        top.add(label, BorderLayout.WEST);
        top.add(icon, BorderLayout.EAST);

        JLabel number = new JLabel(value);
        number.setForeground(WHITE);
        number.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel sub = new JLabel(detail);
        sub.setForeground(new Color(83, 88, 87));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JPanel valuePanel = new JPanel();
        valuePanel.setOpaque(false);
        valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.Y_AXIS));
        valuePanel.add(number);
        valuePanel.add(Box.createVerticalStrut(6));
        valuePanel.add(sub);

        JPanel accentLine = new JPanel(new BorderLayout());
        accentLine.setBackground(accent);
        accentLine.setPreferredSize(new Dimension(0, 3));

        p.add(top, BorderLayout.NORTH);
        p.add(valuePanel, BorderLayout.CENTER);
        p.add(accentLine, BorderLayout.SOUTH);
        return p;
    }

    private JPanel alert(String heading, String detail, Color accent) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(new Color(235, 224, 199));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel line = new JPanel();
        line.setBackground(accent);
        line.setPreferredSize(new Dimension(3, 35));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(heading);
        title.setForeground(new Color(39, 49, 56));
        title.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel sub = new JLabel(detail);
        sub.setForeground(new Color(83, 88, 87));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(sub);

        p.add(line, BorderLayout.WEST);
        p.add(textPanel, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 67));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    // =============== SPARE PARTS INVENTORY ===============

    // Keep parts.txt in its original 4-column format so existing data remains compatible.
    // Additional inventory information is stored separately:
    // parts_minimum.txt: Part ID, Minimum Stock Level
    // parts_movements.txt: Timestamp, Type, Part ID, Part Name, Quantity, Before, After, Reference, Notes
    private static final int DEFAULT_REORDER_LEVEL = 5;

    private void showSpareParts() {
        JPanel root = new JPanel(new BorderLayout(0, 15));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 25, 24, 25));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));
        JLabel h = new JLabel("Spare Parts & Inventory");
        h.setForeground(WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel sub = new JLabel("Track live stock, issue parts to service jobs, restock supplies and review stock movements.");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        headingText.add(h);
        headingText.add(Box.createVerticalStrut(6));
        headingText.add(sub);
        heading.add(headingText, BorderLayout.WEST);

        JButton historyButton = button("Stock Movement History", false);
        heading.add(historyButton, BorderLayout.EAST);
        root.add(heading, BorderLayout.NORTH);

        JPanel main = cardPanel();
        main.setLayout(new BorderLayout(0, 12));
        main.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel summary = new JPanel(new GridLayout(1, 3, 10, 0));
        summary.setOpaque(false);
        JLabel totalParts = inventorySummary("TOTAL PART TYPES", "0", BLUE);
        JLabel lowParts = inventorySummary("LOW STOCK / RESTOCK", "0", ORANGE);
        JLabel outParts = inventorySummary("OUT OF STOCK", "0", RED);
        summary.add(summaryCard(totalParts, BLUE));
        summary.add(summaryCard(lowParts, ORANGE));
        summary.add(summaryCard(outParts, RED));

        JTextField search = new JTextField(18);
        search.setForeground(WHITE);
        search.setCaretColor(TEAL);
        search.setBackground(BG);
        search.setFont(new Font("SansSerif", Font.PLAIN, 12));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 10, 8, 10)));
        JButton searchButton = button("Search", false);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(search);
        searchPanel.add(searchButton);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(title("LIVE INVENTORY REGISTER"), BorderLayout.WEST);
        toolbar.add(searchPanel, BorderLayout.EAST);

        String[] columns = {"PART ID", "PART NAME", "ON HAND", "REORDER LEVEL", "UNIT PRICE (PKR)", "STOCK VALUE (PKR)", "STATUS"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = createTable(tableModel);

        JLabel footerCount = new JLabel();
        footerCount.setForeground(MUTED);
        footerCount.setFont(new Font("SansSerif", Font.PLAIN, 10));
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(footerCount, BorderLayout.WEST);

        Runnable load = () -> {
            tableModel.setRowCount(0);
            int total = 0, low = 0, out = 0;
            String q = search.getText().trim().toLowerCase(Locale.ROOT);
            try {
                for (String[] part : read("parts.txt", 4)) {
                    int qty = Integer.parseInt(part[2]);
                    int minimum = minimumForPart(part[0]);
                    double price = Double.parseDouble(part[3]);
                    String status = qty <= 0 ? "OUT OF STOCK" : (qty <= minimum ? "LOW STOCK" : "IN STOCK");
                    String[] display = {
                            part[0], part[1], String.valueOf(qty), String.valueOf(minimum),
                            money(price), money(qty * price), status
                    };
                    if (q.isEmpty() || String.join(" ", display).toLowerCase(Locale.ROOT).contains(q)) {
                        tableModel.addRow(display);
                    }
                    total++;
                    if (qty <= 0) out++;
                    else if (qty <= minimum) low++;
                }
                totalParts.setText("" + total);
                lowParts.setText("" + low);
                outParts.setText("" + out);
                footerCount.setText("SHOWING " + tableModel.getRowCount() + " OF " + total + " PART TYPE(S)  •  REORDER ALERTS: " + (low + out));
            } catch (Exception ex) {
                error("Could not load inventory: " + ex.getMessage());
            }
        };
        load.run();

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        actions.setOpaque(false);
        JButton add = button("+ Add Part", true);
        JButton issue = button("Issue to Service Job", true);
        JButton restock = button("Restock Selected", false);
        JButton reorder = button("Set Reorder Level", false);
        JButton delete = button("Delete Zero-Stock Part", false);
        JButton reload = button("↻ Reload", false);
        actions.add(add);
        actions.add(issue);
        actions.add(restock);
        actions.add(reorder);
        actions.add(delete);
        actions.add(reload);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(summary, BorderLayout.NORTH);
        JPanel lowerTop = new JPanel(new BorderLayout(0, 10));
        lowerTop.setOpaque(false);
        lowerTop.add(toolbar, BorderLayout.NORTH);
        lowerTop.add(actions, BorderLayout.SOUTH);
        top.add(lowerTop, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(footer, BorderLayout.SOUTH);
        root.add(main, BorderLayout.CENTER);

        searchButton.addActionListener(e -> load.run());
        search.addActionListener(e -> load.run());
        reload.addActionListener(e -> load.run());
        historyButton.addActionListener(e -> showStockMovementHistory());

        add.addActionListener(e -> {
            JTextField id = inventoryField();
            JTextField name = inventoryField();
            JTextField qty = inventoryField();
            JTextField price = inventoryField();
            JTextField minimum = inventoryField();
            JPanel form = new JPanel(new GridLayout(5, 2, 10, 9));
            form.setBackground(PANEL);
            addFormRow(form, "Part ID (e.g. PART008)", id);
            addFormRow(form, "Part Name", name);
            addFormRow(form, "Opening Quantity", qty);
            addFormRow(form, "Unit Price (PKR)", price);
            addFormRow(form, "Minimum Stock Level", minimum);
            minimum.setText(String.valueOf(DEFAULT_REORDER_LEVEL));
            int result = JOptionPane.showConfirmDialog(this, form, "Add Inventory Part",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            String[] row = {id.getText().trim(), name.getText().trim(), qty.getText().trim(), price.getText().trim()};
            try {
                validateRecord("Spare Parts", row);
                int min = parseIntField(minimum.getText().trim(), "Minimum stock level");
                if (min < 0 || min > 1_000_000) throw new IllegalArgumentException("Minimum stock level must be between 0 and 1,000,000.");
                for (String[] existing : read("parts.txt", 4)) {
                    if (existing[0].equalsIgnoreCase(row[0])) throw new IllegalArgumentException("That Part ID already exists.");
                }
                save("parts.txt", row);
                setMinimumForPart(row[0], min);
                int opening = Integer.parseInt(row[2]);
                if (opening > 0) appendStockMovement("OPENING STOCK", row[0], row[1], opening, 0, opening, "", "Initial inventory entry");
                load.run();
                JOptionPane.showMessageDialog(this, "Part added to inventory successfully.", "AutoSync", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        issue.addActionListener(e -> {
            String partId = selectedPartId(table);
            if (partId == null) return;
            String[] part = findPart(partId);
            if (part == null) { error("Selected part could not be found."); return; }
            JTextField jobId = inventoryField();
            JTextField qty = inventoryField();
            JTextField notes = inventoryField();
            JPanel form = new JPanel(new GridLayout(3, 2, 10, 9));
            form.setBackground(PANEL);
            addFormRow(form, "Existing Service Job ID", jobId);
            addFormRow(form, "Quantity to Issue", qty);
            addFormRow(form, "Notes (optional)", notes);
            int result = JOptionPane.showConfirmDialog(this, form,
                    "Issue " + part[1] + " to a Service Job", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            try {
                String job = jobId.getText().trim();
                if (job.isEmpty() || !exists("jobs.txt", 6, job)) throw new IllegalArgumentException("Enter an existing Service Job ID.");
                int amount = parseIntField(qty.getText().trim(), "Quantity to issue");
                if (amount <= 0) throw new IllegalArgumentException("Quantity to issue must be greater than zero.");
                int before = Integer.parseInt(part[2]);
                if (amount > before) throw new IllegalArgumentException("Insufficient stock. Available quantity: " + before + ".");
                changePartStock(partId, -amount, "ISSUED TO JOB", job, notes.getText().trim());
                load.run();
                JOptionPane.showMessageDialog(this, amount + " unit(s) issued to " + job + ". Stock updated automatically.", "Stock Updated", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        restock.addActionListener(e -> {
            String partId = selectedPartId(table);
            if (partId == null) return;
            String[] part = findPart(partId);
            if (part == null) { error("Selected part could not be found."); return; }
            JTextField qty = inventoryField();
            JTextField supplier = inventoryField();
            JTextField notes = inventoryField();
            JPanel form = new JPanel(new GridLayout(3, 2, 10, 9));
            form.setBackground(PANEL);
            addFormRow(form, "Quantity Received", qty);
            addFormRow(form, "Supplier / Purchase Ref", supplier);
            addFormRow(form, "Notes (optional)", notes);
            int result = JOptionPane.showConfirmDialog(this, form,
                    "Restock " + part[1], JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;
            try {
                int amount = parseIntField(qty.getText().trim(), "Quantity received");
                if (amount <= 0) throw new IllegalArgumentException("Restock quantity must be greater than zero.");
                changePartStock(partId, amount, "RESTOCK", supplier.getText().trim(), notes.getText().trim());
                load.run();
                JOptionPane.showMessageDialog(this, "Restock recorded. Inventory quantity increased by " + amount + ".", "Stock Updated", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        reorder.addActionListener(e -> {
            String partId = selectedPartId(table);
            if (partId == null) return;
            String[] part = findPart(partId);
            if (part == null) { error("Selected part could not be found."); return; }
            String value = JOptionPane.showInputDialog(this,
                    "Set the minimum quantity that should trigger a restock alert for " + part[1] + ":",
                    String.valueOf(minimumForPart(partId)));
            if (value == null) return;
            try {
                int min = parseIntField(value.trim(), "Minimum stock level");
                if (min < 0 || min > 1_000_000) throw new IllegalArgumentException("Minimum stock level must be between 0 and 1,000,000.");
                setMinimumForPart(partId, min);
                load.run();
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        delete.addActionListener(e -> {
            String partId = selectedPartId(table);
            if (partId == null) return;
            try {
                String[] part = findPart(partId);
                if (part == null) throw new IllegalArgumentException("Selected part could not be found.");
                if (Integer.parseInt(part[2]) != 0) throw new IllegalArgumentException("For stock safety, only a part with zero quantity can be deleted. Issue or adjust its stock first.");
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Delete " + part[1] + " (" + partId + ")? Movement history will be retained.",
                        "Confirm Part Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                List<String[]> rows = read("parts.txt", 4);
                rows.removeIf(r -> r[0].equalsIgnoreCase(partId));
                writeRows("parts.txt", rows);
                removeMinimumForPart(partId);
                load.run();
            } catch (Exception ex) { error(ex.getMessage()); }
        });

        setContent(root);
    }

    private JLabel inventorySummary(String heading, String value, Color accent) {
        JLabel label = new JLabel(value);
        label.setForeground(WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setToolTipText(heading);
        return label;
    }

    private JPanel summaryCard(JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                new EmptyBorder(9, 12, 9, 12)));
        JLabel heading = new JLabel(value.getToolTipText());
        heading.setForeground(MUTED);
        heading.setFont(new Font("SansSerif", Font.BOLD, 9));
        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JTextField inventoryField() {
        JTextField field = new JTextField(18);
        field.setForeground(WHITE);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEAL);
        return field;
    }

    private void addFormRow(JPanel form, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setForeground(WHITE);
        form.add(l);
        form.add(field);
    }

    private String selectedPartId(JTable table) {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            error("Select a part from the inventory table first.");
            return null;
        }
        return String.valueOf(table.getValueAt(selected, 0));
    }

    private String[] findPart(String partId) {
        try {
            for (String[] part : read("parts.txt", 4)) {
                if (part[0].equalsIgnoreCase(partId)) return part;
            }
        } catch (IOException ignored) { }
        return null;
    }

    private int minimumForPart(String partId) {
        try {
            for (String[] row : read("parts_minimum.txt", 2)) {
                if (row[0].equalsIgnoreCase(partId)) return Integer.parseInt(row[1]);
            }
        } catch (Exception ignored) { }
        return DEFAULT_REORDER_LEVEL;
    }

    private boolean isLowStock(String[] part) {
        try { return Integer.parseInt(part[2]) <= minimumForPart(part[0]); }
        catch (Exception ex) { return false; }
    }

    private void setMinimumForPart(String partId, int minimum) throws IOException {
        List<String[]> rows = read("parts_minimum.txt", 2);
        boolean found = false;
        for (String[] row : rows) {
            if (row[0].equalsIgnoreCase(partId)) {
                row[1] = String.valueOf(minimum);
                found = true;
                break;
            }
        }
        if (!found) rows.add(new String[]{partId, String.valueOf(minimum)});
        writeRows("parts_minimum.txt", rows);
    }

    private void removeMinimumForPart(String partId) throws IOException {
        List<String[]> rows = read("parts_minimum.txt", 2);
        rows.removeIf(r -> r[0].equalsIgnoreCase(partId));
        writeRows("parts_minimum.txt", rows);
    }

    private void changePartStock(String partId, int delta, String type, String reference, String notes) throws IOException {
        List<String[]> parts = read("parts.txt", 4);
        String[] target = null;
        for (String[] row : parts) {
            if (row[0].equalsIgnoreCase(partId)) { target = row; break; }
        }
        if (target == null) throw new IllegalArgumentException("Part ID not found.");
        int before = Integer.parseInt(target[2]);
        int after = before + delta;
        if (after < 0) throw new IllegalArgumentException("Stock cannot become negative.");
        if (after > 1_000_000) throw new IllegalArgumentException("Stock cannot exceed 1,000,000 units.");
        target[2] = String.valueOf(after);
        writeRows("parts.txt", parts);
        try {
            appendStockMovement(type, target[0], target[1], Math.abs(delta), before, after, reference, notes);
        } catch (IOException ex) {
            // Roll back the quantity if the audit record cannot be saved.
            target[2] = String.valueOf(before);
            writeRows("parts.txt", parts);
            throw ex;
        }
    }

    private void appendStockMovement(String type, String partId, String partName, int quantity,
                                     int before, int after, String reference, String notes) throws IOException {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        save("parts_movements.txt", new String[]{timestamp, type, partId, partName,
                String.valueOf(quantity), String.valueOf(before), String.valueOf(after),
                reference == null ? "" : reference, notes == null ? "" : notes});
    }

    private void showStockMovementHistory() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(PANEL);
        String[] columns = {"DATE / TIME", "MOVEMENT", "PART ID", "PART NAME", "QTY", "BEFORE", "AFTER", "JOB / SUPPLIER", "NOTES"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            List<String[]> rows = read("parts_movements.txt", 9);
            Collections.reverse(rows);
            for (String[] row : rows) model.addRow(row);
        } catch (Exception ex) { error("Could not read movement history: " + ex.getMessage()); return; }
        JTable table = createTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel note = new JLabel("Every opening-stock, restock and job-issue transaction is recorded here.");
        note.setForeground(MUTED);
        panel.add(note, BorderLayout.SOUTH);
        JDialog dialog = new JDialog(this, "Spare Parts — Stock Movement History", true);
        dialog.setContentPane(panel);
        dialog.setSize(1120, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void writeRows(String filename, List<String[]> rows) throws IOException {
        Files.createDirectories(dataFolder);
        StringBuilder out = new StringBuilder();
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) out.append('\t');
                out.append(clean(row[i] == null ? "" : row[i]));
            }
            out.append(System.lineSeparator());
        }
        Files.write(dataFolder.resolve(filename), out.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    // =============== MANAGEMENT MODULES ===============

    private void showModule(String[] module) {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 25, 24, 25));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel h = new JLabel(module[0] + " directory");
        h.setForeground(WHITE);
        h.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel sub = new JLabel(
                "Manage records, review information and search your workshop data.");
        sub.setForeground(MUTED);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        titles.add(h);
        titles.add(Box.createVerticalStrut(7));
        titles.add(sub);
        heading.add(titles, BorderLayout.WEST);
        root.add(heading, BorderLayout.NORTH);

        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(0, 14));
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel directoryTitle = title("RECORD DIRECTORY");

        JTextField search = new JTextField(18);
        search.setForeground(WHITE);
        search.setCaretColor(TEAL);
        search.setBackground(BG);
        search.setFont(new Font("SansSerif", Font.PLAIN, 12));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 10, 9, 10)));

        JButton searchButton = button("Search", false);
        JPanel searchControls = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 8, 0));
        searchControls.setOpaque(false);
        searchControls.add(search);
        searchControls.add(searchButton);

        toolbar.add(directoryTitle, BorderLayout.WEST);
        toolbar.add(searchControls, BorderLayout.EAST);

        DefaultTableModel model = new DefaultTableModel(
                Arrays.copyOfRange(module, 2, module.length), 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = createTable(model);

        Runnable load = () -> {
            model.setRowCount(0);
            try {
                for (String[] row :
                        read(module[1], module.length - 2)) {
                    model.addRow(row);
                }
            } catch (Exception ex) {
                error("Could not load records: " + ex.getMessage());
            }
        };

        load.run();

        searchButton.addActionListener(e -> {
            String q = search.getText().trim().toLowerCase(Locale.ROOT);
            model.setRowCount(0);

            try {
                for (String[] row :
                        read(module[1], module.length - 2)) {
                    if (q.isEmpty() ||
                            String.join(" ", row).toLowerCase(Locale.ROOT)
                                    .contains(q)) {
                        model.addRow(row);
                    }
                }
            } catch (Exception ex) {
                error("Search failed: " + ex.getMessage());
            }
        });

        search.addActionListener(e -> searchButton.doClick());

        JButton add = button("+  Add Record", true);
        JButton delete = button("Delete Selected", false);
        JButton reload = button("↻  Reload", false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(add);
        actions.add(delete);
        actions.add(reload);

        JPanel north = new JPanel(new BorderLayout(0, 14));
        north.setOpaque(false);
        north.add(toolbar, BorderLayout.NORTH);
        north.add(actions, BorderLayout.SOUTH);

        JLabel count = new JLabel();
        count.setForeground(MUTED);
        count.setFont(new Font("SansSerif", Font.PLAIN, 10));

        Runnable updateCount = () ->
                count.setText("DISPLAYING " + model.getRowCount()
                        + " RECORD(S)");

        updateCount.run();

        JPanel tableFooter = new JPanel(new BorderLayout());
        tableFooter.setOpaque(false);
        tableFooter.add(count, BorderLayout.WEST);

        panel.add(north, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(tableFooter, BorderLayout.SOUTH);

        add.addActionListener(e -> {
            JTextField[] fields = new JTextField[module.length - 2];
            JPanel form = new JPanel(new GridLayout(
                    fields.length, 2, 10, 10));
            form.setBackground(PANEL);

            for (int i = 0; i < fields.length; i++) {
                JLabel label = new JLabel(module[i + 2]);
                label.setForeground(WHITE);
                form.add(label);

                fields[i] = new JTextField(18);
                form.add(fields[i]);
            }

            int result = JOptionPane.showConfirmDialog(
                    this, form, "Create " + module[0] + " record",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return;

            String[] row = new String[fields.length];

            for (int i = 0; i < fields.length; i++) {
                row[i] = fields[i].getText().trim();
                if (row[i].isEmpty()) {
                    error(module[i + 2] + " is required.");
                    return;
                }
            }

            try {
                validateRecord(module[0], row);

                for (String[] old : read(module[1], module.length - 2)) {
                    if (!module[0].equals("Maintenance")
                            && old[0].equalsIgnoreCase(row[0])) {
                        throw new IllegalArgumentException("This ID already exists.");
                    }
                    if (module[0].equals("Vehicles")
                            && old[1].equalsIgnoreCase(row[1])) {
                        throw new IllegalArgumentException("This vehicle registration already exists.");
                    }
                    if (module[0].equals("Maintenance")
                            && old[0].equalsIgnoreCase(row[0])
                            && old[1].equalsIgnoreCase(row[1])) {
                        throw new IllegalArgumentException(
                                "This maintenance recommendation already exists for this vehicle.");
                    }
                }

                validateReferences(module[0], row);

                if (module[0].equals("Customers")) {
                    for (String[] old : read(module[1], module.length - 2)) {
                        if (old[2].equals(row[2])) {
                            throw new IllegalArgumentException("This phone number is already registered.");
                        }
                        if (old[3].equalsIgnoreCase(row[3])) {
                            throw new IllegalArgumentException("This email address is already registered.");
                        }
                    }
                }

                if (module[0].equals("Invoices")) {
                    double labor = Double.parseDouble(row[3]);
                    double parts = Double.parseDouble(row[4]);
                    double discount = Double.parseDouble(row[5]);

                    if (discount > labor + parts) {
                        throw new IllegalArgumentException(
                                "Discount cannot exceed the subtotal.");
                    }

                    row[6] = String.format(Locale.US, "%.2f",
                            labor + parts - discount);
                }

                save(module[1], row);
                navigate(module[0]);

                JOptionPane.showMessageDialog(this,
                        "Record saved successfully.",
                        "AutoSync", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                error(ex.getMessage());
            }
        });

        delete.addActionListener(e -> {
            int selected = table.getSelectedRow();

            if (selected < 0) {
                error("Select a record to delete.");
                return;
            }

            int answer = JOptionPane.showConfirmDialog(
                    this, "Delete the selected record permanently?",
                    "Confirm deletion", JOptionPane.YES_NO_OPTION);

            if (answer != JOptionPane.YES_OPTION) return;

            model.removeRow(table.convertRowIndexToModel(selected));

            try {
                overwrite(module[1], model);
                updateCount.run();
                navigate(module[0]);
            } catch (Exception ex) {
                error("Could not delete record: " + ex.getMessage());
            }
        });

        reload.addActionListener(e -> {
            load.run();
            updateCount.run();
        });

        root.add(panel, BorderLayout.CENTER);
        setContent(root);
    }

    // =============== VALIDATION ===============

    private void validateRecord(String module, String[] r) {
        // General validation: reject blank values and unsafe field separators.
        for (int i = 0; i < r.length; i++) {
            r[i] = r[i].trim();
            if (r[i].isEmpty()) {
                throw new IllegalArgumentException(
                        "All fields are required. Please complete: " + fieldName(module, i));
            }
            if (r[i].contains("\t") || r[i].contains("\n") || r[i].contains("\r")) {
                throw new IllegalArgumentException("Tabs and line breaks are not allowed in fields.");
            }
        }

        switch (module) {
            case "Customers":
                if (!r[1].matches("[\\p{L}][\\p{L} .'-]{1,59}")) {
                    throw new IllegalArgumentException(
                            "Enter a valid customer name (2–60 letters; spaces, apostrophes and hyphens are allowed).");
                }
                if (!r[2].matches("\\d{11}")) {
                    throw new IllegalArgumentException(
                            "Phone number must contain exactly 11 digits, e.g. 03001234567. Do not enter spaces or +92.");
                }
                if (!r[3].matches("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$")) {
                    throw new IllegalArgumentException(
                            "Enter a valid email address, e.g. name@example.com.");
                }
                r[3] = r[3].toLowerCase(Locale.ROOT);
                break;

            case "Vehicles":
                if (!r[1].matches("[A-Za-z0-9 -]{2,20}")) {
                    throw new IllegalArgumentException(
                            "Registration must be 2–20 characters and contain only letters, digits, spaces or hyphens.");
                }
                if (!r[2].matches("[\\p{L}\\p{N} .,'()/-]{2,60}")) {
                    throw new IllegalArgumentException("Enter a valid make/model (2–60 characters).");
                }
                int year = parseIntField(r[3], "Year");
                double mileage = parseNonNegativeDouble(r[4], "Mileage");
                int currentYear = java.time.Year.now().getValue();
                if (year < 1886 || year > currentYear + 1) {
                    throw new IllegalArgumentException(
                            "Year must be between 1886 and " + (currentYear + 1) + ".");
                }
                r[1] = r[1].toUpperCase(Locale.ROOT);
                r[3] = String.valueOf(year);
                r[4] = decimal(mileage);
                break;

            case "Mechanics":
                if (!r[1].matches("[\\p{L}][\\p{L} .'-]{1,59}")) {
                    throw new IllegalArgumentException("Enter a valid mechanic name.");
                }
                if (!r[2].matches("[\\p{L}\\p{N} &+./'-]{2,60}")) {
                    throw new IllegalArgumentException("Enter a valid specialization (2–60 characters).");
                }
                r[3] = normalizeChoice(r[3], "Available", "Busy", "Off Duty");
                break;

            case "Spare Parts":
                if (!r[1].matches("[\\p{L}\\p{N}][\\p{L}\\p{N} .,'()/-]{1,59}")) {
                    throw new IllegalArgumentException("Enter a valid part name (2–60 characters).");
                }
                int quantity = parseIntField(r[2], "Quantity");
                if (quantity < 0 || quantity > 1_000_000) {
                    throw new IllegalArgumentException("Quantity must be between 0 and 1,000,000.");
                }
                double unitPrice = parseNonNegativeDouble(r[3], "Unit price");
                r[2] = String.valueOf(quantity);
                r[3] = decimal(unitPrice);
                break;

            case "Service Jobs":
                if (!r[2].matches("[\\p{L}\\p{N} .,'()&+/-]{2,60}")) {
                    throw new IllegalArgumentException("Enter a valid service type (2–60 characters).");
                }
                r[4] = normalizeChoice(r[4], "Scheduled", "In Progress", "Completed", "Cancelled");
                r[5] = decimal(parseNonNegativeDouble(r[5], "Labor cost"));
                break;

            case "Invoices":
                r[3] = decimal(parseNonNegativeDouble(r[3], "Labor"));
                r[4] = decimal(parseNonNegativeDouble(r[4], "Parts"));
                r[5] = decimal(parseNonNegativeDouble(r[5], "Discount"));
                double labor = Double.parseDouble(r[3]);
                double parts = Double.parseDouble(r[4]);
                double discount = Double.parseDouble(r[5]);
                if (discount > labor + parts) {
                    throw new IllegalArgumentException("Discount cannot exceed the invoice subtotal.");
                }
                break;

            case "Maintenance":
                if (!r[1].matches("[\\p{L}\\p{N} .,'()&+/-]{3,100}")) {
                    throw new IllegalArgumentException("Enter a valid maintenance recommendation (3–100 characters).");
                }
                r[2] = decimal(parseNonNegativeDouble(r[2], "Due mileage"));
                r[3] = normalizeChoice(r[3], "Pending", "Scheduled", "Completed", "Overdue");
                break;

            default:
                throw new IllegalArgumentException("Unknown record type.");
        }
    }

    private String fieldName(String module, int index) {
        for (String[] m : modules) {
            if (m[0].equals(module) && index + 2 < m.length) {
                return m[index + 2];
            }
        }
        return "field";
    }

    private int parseIntField(String value, String label) {
        try {
            if (!value.matches("\\d{1,9}")) {
                throw new NumberFormatException();
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be a whole number using digits only.");
        }
    }

    private double parseNonNegativeDouble(String value, String label) {
        try {
            if (!value.matches("\\d{1,12}(?:\\.\\d{1,2})?")) {
                throw new NumberFormatException();
            }
            double number = Double.parseDouble(value);
            if (!Double.isFinite(number) || number < 0 || number > 1_000_000_000_000d) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    label + " must be a non-negative number (up to 2 decimal places).");
        }
    }

    private String decimal(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String normalizeChoice(String value, String... choices) {
        for (String choice : choices) {
            if (choice.equalsIgnoreCase(value.trim())) {
                return choice;
            }
        }
        throw new IllegalArgumentException(
                "Invalid value '" + value + "'. Allowed values: " + String.join(", ", choices) + ".");
    }

    private void validateReferences(String module, String[] r)
            throws IOException {

        if (module.equals("Vehicles") &&
                !exists("customers.txt", 4, r[5])) {
            throw new IllegalArgumentException(
                    "Customer ID not found. Add the customer first.");
        }

        if (module.equals("Maintenance") &&
                !exists("vehicles.txt", 6, r[0])) {
            throw new IllegalArgumentException(
                    "Vehicle ID not found. Add the vehicle first.");
        }

        if (module.equals("Service Jobs")) {
            if (!exists("vehicles.txt", 6, r[1])) {
                throw new IllegalArgumentException(
                        "Vehicle ID not found.");
            }

            if (!exists("mechanics.txt", 4, r[3])) {
                throw new IllegalArgumentException(
                        "Mechanic ID not found.");
            }
        }

        if (module.equals("Invoices")) {
            if (!exists("jobs.txt", 6, r[1])) {
                throw new IllegalArgumentException(
                        "Service Job ID not found.");
            }

            if (!exists("customers.txt", 4, r[2])) {
                throw new IllegalArgumentException(
                        "Customer ID not found.");
            }
        }
    }

    private boolean exists(String filename, int columns, String id)
            throws IOException {
        for (String[] row : read(filename, columns)) {
            if (row[0].equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    // =============== TABLE STYLING ===============

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("SansSerif", Font.PLAIN, 11));
        table.setForeground(new Color(31, 37, 41));   // Gunmetal text
        table.setBackground(new Color(249, 243, 229)); // Cream table
        table.setGridColor(new Color(190, 148, 52)); // Granite Gray grid
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(190, 148, 52)); // Granite Gray
        table.setSelectionForeground(new Color(31, 37, 41));    // Gunmetal
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0, 38));
        h.setBackground(new Color(226, 211, 174));
        h.setForeground(new Color(39, 49, 56));
        h.setFont(new Font("SansSerif", Font.BOLD, 10));
        h.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, BORDER));

        JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(
                JScrollPane.class, table);

        if (scroll != null) {
            scroll.getViewport().setBackground(PANEL);
            scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        }

        return table;
    }

    private JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createLineBorder(BORDER));
        return p;
    }

    private JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(WHITE);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        return l;
    }



    private JButton button(String text, boolean primary) {
        JButton b = new JButton(text);

        Color bg = primary ? new Color(31, 37, 41) : new Color(249, 243, 229);
        Color fg = primary ? new Color(249, 243, 229) : new Color(39, 49, 56);

        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 148, 52)),
                new EmptyBorder(8, 13, 8, 13)));
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        b.addChangeListener(e -> {
            ButtonModel model = b.getModel();
            if (model.isPressed()) {
                b.setBackground(new Color(155, 116, 35));
                b.setForeground(Color.WHITE);
            } else if (model.isRollover()) {
                b.setBackground(new Color(190, 148, 52));
                b.setForeground(new Color(31, 37, 41));
            } else {
                b.setBackground(bg);
                b.setForeground(fg);
            }
        });
        return b;
    }

    // =============== FILE STORAGE ===============

    private List<String[]> read(String filename, int columns)
            throws IOException {
        List<String[]> records = new ArrayList<>();
        Path file = dataFolder.resolve(filename);

        if (!Files.exists(file)) return records;

        for (String line :
                Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String[] row = line.split("\t", -1);
            if (row.length == columns) records.add(row);
        }

        return records;
    }

    private void save(String filename, String[] row)
            throws IOException {
        Files.createDirectories(dataFolder);

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < row.length; i++) {
            if (i > 0) line.append('\t');
            line.append(clean(row[i]));
        }

        line.append(System.lineSeparator());

        Files.write(dataFolder.resolve(filename),
                line.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private void overwrite(String filename, DefaultTableModel model)
            throws IOException {
        Files.createDirectories(dataFolder);
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                if (j > 0) text.append('\t');
                text.append(clean(String.valueOf(
                        model.getValueAt(i, j))));
            }
            text.append(System.lineSeparator());
        }

        Files.write(dataFolder.resolve(filename),
                text.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String clean(String s) {
        return s.replace("\t", " ")
                .replace("\r", " ")
                .replace("\n", " ");
    }

    private int count(String file, int columns) {
        try {
            return read(file, columns).size();
        } catch (Exception e) {
            return 0;
        }
    }

    private String money(double value) {
        return "Rs. " + NumberFormat.getNumberInstance(Locale.US)
                .format(value);
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this,
                message == null ? "An unexpected error occurred." : message,
                "AutoSync", JOptionPane.WARNING_MESSAGE);
    }

    // =============== LOGIN MODULE ===============

    private static class LoginDialog extends JDialog {
        private boolean authenticated = false;

        private final Color charcoal = new Color(31, 37, 41);
        private final Color ivory = new Color(249, 243, 229);
        private final Color warmIvory = new Color(243, 235, 216);
        private final Color gold = new Color(190, 148, 52);
        private final Color muted = new Color(112, 116, 116);

        LoginDialog() {
            setTitle("AutoSync | Secure Login");
            setModal(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setResizable(false);

            // A full-window login experience rather than a small floating dialog.
            Rectangle usableBounds = GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getMaximumWindowBounds();
            setBounds(usableBounds);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(warmIvory);

            JPanel topBar = new JPanel(new BorderLayout());
            topBar.setBackground(charcoal);
            topBar.setBorder(new EmptyBorder(18, 34, 18, 34));

            JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            brand.setOpaque(false);
            brand.add(new BrandMark(42, 42));

            JLabel brandName = new JLabel("AutoSync");
            brandName.setForeground(ivory);
            brandName.setFont(new Font("SansSerif", Font.BOLD, 27));
            brand.add(brandName);

            JLabel brandSub = new JLabel("  |  WORKSHOP COMMAND CENTER");
            brandSub.setForeground(new Color(211, 211, 205));
            brandSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
            brand.add(brandSub);

            topBar.add(brand, BorderLayout.WEST);
            root.add(topBar, BorderLayout.NORTH);

            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(new Color(236, 228, 210));
            center.setBorder(new EmptyBorder(30, 20, 30, 20));

            JPanel loginCard = new JPanel(new BorderLayout(0, 20));
            loginCard.setBackground(ivory);
            loginCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(218, 205, 177), 1),
                    new EmptyBorder(35, 42, 32, 42)));
            loginCard.setPreferredSize(new Dimension(490, 500));

            JPanel heading = new JPanel();
            heading.setOpaque(false);
            heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

            JPanel markRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            markRow.setOpaque(false);
            markRow.add(new BrandMark(58, 58));
            markRow.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = new JLabel("Welcome Back");
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            title.setForeground(charcoal);
            title.setFont(new Font("SansSerif", Font.BOLD, 28));

            JLabel subtitle = new JLabel("Sign in to your workshop workspace");
            subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitle.setForeground(muted);
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

            JPanel goldLine = new JPanel();
            goldLine.setBackground(gold);
            goldLine.setMaximumSize(new Dimension(72, 4));
            goldLine.setPreferredSize(new Dimension(72, 4));
            goldLine.setAlignmentX(Component.CENTER_ALIGNMENT);

            heading.add(markRow);
            heading.add(Box.createVerticalStrut(12));
            heading.add(title);
            heading.add(Box.createVerticalStrut(7));
            heading.add(subtitle);
            heading.add(Box.createVerticalStrut(17));
            heading.add(goldLine);

            JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
            form.setOpaque(false);

            JLabel userLabel = new JLabel("USERNAME");
            userLabel.setForeground(charcoal);
            userLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            JTextField username = new JTextField();
            styleField(username);

            JLabel passLabel = new JLabel("PASSWORD");
            passLabel.setForeground(charcoal);
            passLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            JPasswordField password = new JPasswordField();
            styleField(password);

            form.add(userLabel);
            form.add(username);
            form.add(passLabel);
            form.add(password);

            JButton loginButton = new JButton("SIGN IN");
            loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            loginButton.setForeground(ivory);
            loginButton.setBackground(charcoal);
            loginButton.setOpaque(true);
            loginButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            loginButton.setFocusPainted(false);
            loginButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(gold),
                    new EmptyBorder(12, 15, 12, 15)));
            loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    loginButton.setBackground(gold);
                    loginButton.setForeground(charcoal);
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    loginButton.setBackground(charcoal);
                    loginButton.setForeground(ivory);
                }
            });

            JLabel hint = new JLabel("Demo login: admin  /  AutoSync@123");
            hint.setHorizontalAlignment(SwingConstants.CENTER);
            hint.setForeground(muted);
            hint.setFont(new Font("SansSerif", Font.PLAIN, 11));

            JPanel bottom = new JPanel(new BorderLayout(0, 12));
            bottom.setOpaque(false);
            bottom.add(loginButton, BorderLayout.NORTH);
            bottom.add(hint, BorderLayout.SOUTH);

            loginCard.add(heading, BorderLayout.NORTH);
            loginCard.add(form, BorderLayout.CENTER);
            loginCard.add(bottom, BorderLayout.SOUTH);
            center.add(loginCard);

            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(charcoal);
            footer.setBorder(new EmptyBorder(15, 34, 15, 34));

            JLabel footerLeft = new JLabel("AUTOSYNC  •  WORKSHOP MANAGEMENT PLATFORM");
            footerLeft.setForeground(new Color(211, 211, 205));
            footerLeft.setFont(new Font("SansSerif", Font.BOLD, 10));

            JLabel footerRight = new JLabel("SECURE WORKSPACE ACCESS");
            footerRight.setForeground(gold);
            footerRight.setFont(new Font("SansSerif", Font.BOLD, 10));

            footer.add(footerLeft, BorderLayout.WEST);
            footer.add(footerRight, BorderLayout.EAST);

            root.add(center, BorderLayout.CENTER);
            root.add(footer, BorderLayout.SOUTH);
            setContentPane(root);

            loginButton.addActionListener(e -> authenticate(username, password));
            password.addActionListener(e -> authenticate(username, password));
            getRootPane().setDefaultButton(loginButton);
        }

        private void styleField(JTextField field) {
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setForeground(new Color(39, 49, 56));
            field.setBackground(Color.WHITE);
            field.setCaretColor(new Color(190, 148, 52));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 201, 181)),
                    new EmptyBorder(10, 11, 10, 11)));
        }

        private void authenticate(JTextField username, JPasswordField password) {
            String user = username.getText().trim();
            String pass = new String(password.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password are both required.",
                        "Login validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Demo credentials for the academic project.
            if (user.equals("admin") && pass.equals("AutoSync@123")) {
                authenticated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Incorrect username or password. Please try again.",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
                password.setText("");
                password.requestFocusInWindow();
            }
        }

        boolean showLogin() {
            setVisible(true);
            return authenticated;
        }
    }

    // Custom AutoSync brand mark: two interlocking diamonds in ivory and gold.
    private static class BrandMark extends JPanel {
        private final int markWidth;
        private final int markHeight;

        BrandMark(int width, int height) {
            markWidth = width;
            markHeight = height;
            setOpaque(false);
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int s = Math.min(markWidth, markHeight) / 2 - 2;
            int cx = markWidth / 2;
            int cy = markHeight / 2;
            Polygon left = new Polygon(
                    new int[]{cx - s / 2, cx, cx - s / 2, cx - s},
                    new int[]{cy - s, cy - s / 2, cy, cy - s / 2}, 4);
            Polygon right = new Polygon(
                    new int[]{cx, cx + s / 2, cx + s, cx + s / 2},
                    new int[]{cy - s / 2, cy - s, cy - s / 2, cy}, 4);

            g2.setColor(new Color(249, 243, 229));
            g2.fillPolygon(left);
            g2.setColor(new Color(190, 148, 52));
            g2.fillPolygon(right);
            g2.setColor(new Color(190, 148, 52));
            g2.setStroke(new BasicStroke(Math.max(1.2f, s / 10f)));
            g2.drawPolygon(left);
            g2.dispose();
        }
    }

    // =============== MAIN ===============

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            LoginDialog login = new LoginDialog();
            if (login.showLogin()) {
                new AutoSyncApp().setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

