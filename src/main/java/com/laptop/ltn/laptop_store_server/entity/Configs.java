package com.laptop.ltn.laptop_store_server.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configs {
    private ConfigProduct aiChip = new ConfigProduct("Thông tin chip AI", "Chip AI", 0);
    private ConfigProduct cpu = new ConfigProduct("Thông tin CPU", "CPU", 1);
    private ConfigProduct graphicCard = new ConfigProduct("Thông tin Card đồ họa", "Card đồ họa", 2);
    private ConfigProduct ram = new ConfigProduct("8GB", "Dung lượng RAM", "RAM", 3);
    private ConfigProduct hardDrive = new ConfigProduct("256GB SSD", "Loại ổ cứng", "Ổ cứng", 4);
    private ConfigProduct refreshRate = new ConfigProduct("Tần số quét màn hình", "Tần số quét", 5);
    private ConfigProduct panel = new ConfigProduct("Chất liệu tấm nền", "Chất liệu tấm nền", 6);
    private ConfigProduct screenTechnology = new ConfigProduct("Công nghệ màn hình", "Công nghệ màn hình", 7);
    private ConfigProduct screen = new ConfigProduct("15.6 inch", "Kích thước màn hình", "Màn hình", 8);
    private ConfigProduct resolution = new ConfigProduct("1920x1080", "Độ phân giải", 9);
    private ConfigProduct audioTechnology = new ConfigProduct("Công nghệ âm thanh", "Công nghệ âm thanh", 10);
    private ConfigProduct connectionPort = new ConfigProduct("Cổng kết nối hỗ trợ", "Cổng kết nối", 11);
    private ConfigProduct bluetooth = new ConfigProduct("Chuẩn Bluetooth", "Bluetooth", 12);
    private ConfigProduct material = new ConfigProduct("Chất liệu khung máy", "Chất liệu", 13);
    private ConfigProduct size = new ConfigProduct("35 x 24 x 2 cm", "Kích thước sản phẩm", "Kích thước", 14);
    private ConfigProduct weight = new ConfigProduct("1.8kg", "Trọng lượng sản phẩm", "Trọng lượng", 15);
    private ConfigProduct specialFeature = new ConfigProduct("Tính năng đặc biệt", "Tính năng đặc biệt", 16);
    private ConfigProduct keyboardLight = new ConfigProduct("Đèn nền bàn phím", "Loại đèn bàn phím", 17);
    private ConfigProduct security = new ConfigProduct("Bảo mật và quyền riêng tư", "Bảo mật", 18);
    private ConfigProduct webcam = new ConfigProduct("Độ phân giải webcam", "WebCam", 19);
    private ConfigProduct operatingSystem = new ConfigProduct("Hệ điều hành mặc định", "Hệ điều hành", 20);
    private ConfigProduct battery = new ConfigProduct("56Wh", "Dung lượng pin", "Pin", 21);
    private ConfigProduct need = new ConfigProduct("Nhu cầu sử dụng", "Nhu cầu", 22);
    private ConfigProduct madeIn = new ConfigProduct("Việt Nam", "Xuất xứ sản phẩm", "Xuất xứ", 23);
    private ConfigProduct yearOfLaunch = new ConfigProduct("2024", "Năm sản xuất", "Năm ra mắt", 24);
}
