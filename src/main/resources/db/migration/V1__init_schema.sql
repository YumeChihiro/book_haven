
CREATE TABLE roles (
    role_id INT PRIMARY KEY,
    role_name ENUM('SHOP', 'ADMIN') NOT NULL
);

CREATE TABLE managers (
    manager_id INT AUTO_INCREMENT PRIMARY KEY,
    manager_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    avatar MEDIUMBLOB,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE subscription_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plan_name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    duration INT NOT NULL -- Số ngày của gói
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE subscription_benefits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plan_id INT NOT NULL,
    benefit_type VARCHAR(50) NOT NULL,
    benefit_value DECIMAL(10,2) NOT NULL,  -- Giá trị của quyền lợi (0 hoặc 1 với Boolean, % hoa hồng,...)
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shop_subscriptions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    manager_id INT NOT NULL,
    plan_id INT NOT NULL,
    start_date DATETIME DEFAULT CURRENT_TIMESTAMP, 
    end_date DATETIME NOT NULL,  
    status ENUM('active', 'expired', 'cancelled') DEFAULT 'active',
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone VARCHAR(20) NOT NULL,
    birth DATE,
    gender ENUM('Male', 'Female'),
    avatar MEDIUMBLOB
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE address (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    road VARCHAR(255),
    town VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    province VARCHAR(255) NOT NULL,
    type ENUM('Home', 'Company') NOT NULL,
    manager_id INT,
    customer_id INT,
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE manager_accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    recent_activity DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('Wait', 'Confirm', 'Warning') NOT NULL,
    manager_id INT UNIQUE,
    customer_id INT UNIQUE,
    CHECK (manager_id IS NOT NULL OR customer_id IS NOT NULL),
    CHECK (NOT (manager_id IS NOT NULL AND customer_id IS NOT NULL)),
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    picture MEDIUMBLOB,
    publisher_id INT NOT NULL,
    author_id INT NOT NULL,
    category_id INT NOT NULL,
    weight DECIMAL(5,2) NOT NULL DEFAULT 0.5,
    FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(author_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shop_product (
    shop_product_id INT AUTO_INCREMENT PRIMARY KEY,
    manager_id INT NOT NULL,
    product_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE (manager_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shippers (
    shipper_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_statuses(
	order_status_id INt AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE vouchers (
    voucher_id INT AUTO_INCREMENT PRIMARY KEY,
    voucher_code VARCHAR(255) NOT NULL,
    discount_percentage DECIMAL(5,2) NOT NULL,
    created_at DATE NOT NULL,
    expiration_date DATE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    delivery_date DATE,
    ship_address VARCHAR(255) NOT NULL,
    sender_address VARCHAR(255) NOT NULL,
    total FLOAT NOT NULL,
    payment_id INT NOT NULL,
    shipper_id INT NOT NULL,
    shipping_fee FLOAT NOT NULL,
    commission_fee FLOAT NOT NULL,
    order_status_id INT NOT NULL,
    manager_id INT NOT NULL,
    customer_id INT NOT NULL,
    
    CONSTRAINT fk_order_payment FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_shipper FOREIGN KEY (shipper_id) REFERENCES shippers(shipper_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_status FOREIGN KEY (order_status_id) REFERENCES order_statuses(order_status_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_manager FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    customer_id INT NOT NULL,
    payment_id INT NOT NULL, 
    amount FLOAT NOT NULL,
    payment_status ENUM('Pending', 'Completed', 'Failed', 'Refunded') NOT NULL DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE
);

CREATE TABLE order_vouchers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    voucher_id INT NOT NULL,
    discount_amount FLOAT NOT NULL,
    total_after_discount FLOAT NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shipping_fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    weight FLOAT NOT NULL, -- Khối lượng hàng hóa áp dụng
    distance FLOAT NOT NULL, -- Khoảng cách giao hàng (km)
    fee FLOAT NOT NULL -- Mức phí vận chuyển tương ứng
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price FLOAT NOT NULL,
    subtotal FLOAT NOT NULL, -- Tổng giá của sản phẩm trong đơn
    weight FLOAT NOT NULL, -- Khối lượng mỗi sản phẩm
    distance FLOAT NOT NULL, -- Khoảng cách giao hàng cho từng sản phẩm
    shipping_fee_id INT DEFAULT NULL, -- Liên kết đến bảng shipping_fees

    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (shipping_fee_id) REFERENCES shipping_fees(fee_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_tracking (
    tracking_id     INT PRIMARY KEY AUTO_INCREMENT,
    order_id        INT NOT NULL,
    order_status_id INT NOT NULL,
    changed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (order_status_id) REFERENCES order_statuses(order_status_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE product_feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    order_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE manager_feedbacks (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    manager_id INT NOT NULL,
    order_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE feedback_reply (
    reply_id INT AUTO_INCREMENT PRIMARY KEY,
    feedback_id INT NOT NULL,
    feedback_type ENUM('Product', 'Manager') NOT NULL, 
    reply_text TEXT NOT NULL,
    replied_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    replied_by VARCHAR(255) NOT NULL,  

    CONSTRAINT fk_reply_feedback_product FOREIGN KEY (feedback_id) REFERENCES product_feedbacks(feedback_id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_feedback_manager FOREIGN KEY (feedback_id) REFERENCES manager_feedbacks(feedback_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE feedback_report (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    report_reason TEXT NOT NULL,
    report_status ENUM('PENDING', 'REVIEWED', 'RESOLVED') NOT NULL DEFAULT 'PENDING',
    reported_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    product_feedback_id INT DEFAULT NULL,
    manager_feedback_id INT DEFAULT NULL,
    reported_by_customer_id INT DEFAULT NULL, -- Đổi tên để rõ ràng
    reported_by_manager_id INT DEFAULT NULL,  -- Thêm cột cho Manager

    CONSTRAINT fk_feedback_report_product FOREIGN KEY (product_feedback_id) 
        REFERENCES product_feedbacks(feedback_id) ON DELETE SET NULL,
    CONSTRAINT fk_feedback_report_manager FOREIGN KEY (manager_feedback_id) 
        REFERENCES manager_feedbacks(feedback_id) ON DELETE SET NULL,
    CONSTRAINT fk_feedback_report_customer FOREIGN KEY (reported_by_customer_id) 
        REFERENCES customers(customer_id) ON DELETE SET NULL,
    CONSTRAINT fk_feedback_report_manager_reporter FOREIGN KEY (reported_by_manager_id) 
        REFERENCES managers(manager_id) ON DELETE SET NULL,
    CONSTRAINT chk_reporter CHECK (
        (reported_by_customer_id IS NOT NULL AND reported_by_manager_id IS NULL) OR 
        (reported_by_customer_id IS NULL AND reported_by_manager_id IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart (
    cart_id       INT PRIMARY KEY AUTO_INCREMENT,
    customer_id   INT UNIQUE NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart_item (
    cart_item_id  INT PRIMARY KEY AUTO_INCREMENT,
    cart_id       INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL CHECK (quantity > 0),
    added_price   FLOAT NOT NULL, -- Giá tại thời điểm thêm vào giỏ hàng
    selected      BOOLEAN DEFAULT FALSE,  -- Người dùng chọn để thanh toán hay chưa
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE(cart_id, product_id),
    FOREIGN KEY (cart_id) REFERENCES cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    customer_id INT NULL,
    manager_id INT NULL,
    sender_manager_id INT NOT NULL,
    CONSTRAINT fk_messages_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    CONSTRAINT fk_messages_manager FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE SET NULL,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE
);


CREATE TABLE login_history (
    login_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    manager_id INT,
    ip_address VARCHAR(45),
    device_info TEXT,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CHECK (customer_id IS NOT NULL OR manager_id IS NOT NULL),
    CHECK (NOT (customer_id IS NOT NULL AND manager_id IS NOT NULL)),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES managers(manager_id) ON DELETE CASCADE
);

CREATE TABLE search_history (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);


CREATE TABLE publishers (
    publisher_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE authors (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth DATE,
    nationality  VARCHAR(255) NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DELIMITER $$

CREATE TRIGGER check_feedback_id_before_insert
BEFORE INSERT ON feedback_reply
FOR EACH ROW
BEGIN
    DECLARE count_feedback INT DEFAULT 0;

    CASE NEW.feedback_type
        WHEN 'Product' THEN 
            SELECT COUNT(*) INTO count_feedback FROM product_feedbacks WHERE feedback_id = NEW.feedback_id;
        WHEN 'Manager' THEN 
            SELECT COUNT(*) INTO count_feedback FROM manager_feedbacks WHERE feedback_id = NEW.feedback_id;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid feedback_type';
    END CASE;

    IF count_feedback = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid feedback_id for the given feedback_type';
    END IF;
END$$

DELIMITER ;











