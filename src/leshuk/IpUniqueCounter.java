package leshuk;

import java.io.*;
import java.util.Scanner;

/**
 * Ip unique counter.
 */
public class IpUniqueCounter {

    private static int[] parseIpToArr(String ipStr) {
        int[] ip = new int[4];
        String[] ipParts = ipStr.split("\\.");

        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(ipParts[i]);
        }

        return ip;
    }

    private static long uniqueIpCount(String url) {
        byte[][][][] ips = new byte[128][128][128][256];

        try {
            File file = new File(url);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);


            long countOp = 0;  //счетчики операций, для понимания, что программа работает
            long countKOp = 1;
            String ipStr = reader.readLine();

            System.out.println("Процедура подсчета уникальных ip-адресов запущена.");
            System.out.println("Обработанно записей (100 миллионов):");
            while (ipStr != null) {
                int[] ipArr = parseIpToArr(ipStr);

                //В зависимости от диапазона, которому принадлежит адрес, помечаем соответствующий бит в байте единичкой
                if (ipArr[0] < 128) {
                    if (ipArr[1] < 128) {
                        if (ipArr[2] < 128) {
                            ips[ipArr[0]][ipArr[1]][ipArr[2]][ipArr[3]] |= (1 << 0);
                        } else {
                            ips[ipArr[0]][ipArr[1]][ipArr[2] - 128][ipArr[3]] |= (1 << 1);
                        }
                    } else { //ipArr1 > 128
                        if (ipArr[2] < 128) {
                            ips[ipArr[0]][ipArr[1] - 128][ipArr[2]][ipArr[3]] |= (1 << 2);
                        } else {
                            ips[ipArr[0]][ipArr[1] - 128][ipArr[2] - 128][ipArr[3]] |= (1 << 3);
                        }
                    }
                } else { //ipArr0 > 128
                    if (ipArr[1] < 128) {
                        if (ipArr[2] < 128) {
                            ips[ipArr[0] - 128][ipArr[1]][ipArr[2]][ipArr[3]] |= (1 << 4);
                        } else {
                            ips[ipArr[0] - 128][ipArr[1]][ipArr[2] - 128][ipArr[3]] |= (1 << 5);
                        }
                    } else {//ipArr1 > 128
                        if (ipArr[2] < 128) {
                            ips[ipArr[0] - 128][ipArr[1] - 128][ipArr[2]][ipArr[3]] |= (1 << 6);
                        } else {
                            ips[ipArr[0] - 128][ipArr[1] - 128][ipArr[2] - 128][ipArr[3]] |= (1 << 7);
                        }
                    }
                }

                ipStr = reader.readLine();

                if (countOp == 100_000_000) {
                    System.out.print(countKOp + " ");
                    if (countKOp % 10 == 0) System.out.println();
                    countKOp++;
                    countOp = 0;
                }
                countOp++;
            }

            System.out.println();
            System.out.println(countKOp + "_" + countOp + " записей всего.");

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found!");
            return -1;
        } catch (
                IOException e) {
            e.printStackTrace();
            System.out.println("Can`t read file!");
            return -1;
        }

        //Считаем количество бит-единичек в массиве
        //Оно же - количество уникальных ip-адресов.
        System.out.println("Подсчет уникальных адресов...");
        long count = 0;
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                for (int k = 0; k < 128; k++) {
                    for (int l = 0; l < 256; l++) {
                        for (int b = 0; b < 8; b++) {
                            count += ((1 << b) & ips[i][j][k][l]) >> b;
                        }
                    }
                }
            }
        }

        return count;
    }

    /**
     * The entry point of application.
     *
     * @param args the input url
     *             Если args пусто, url считывается с консоли
     */
    public static void main(String[] args) {
        String url;
        if (args.length > 0) {
            url = args[0];
        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("Введите путь к файлу с адресами:");
            url = in.nextLine();
        }
//        url = "/media/leshuk/AC_500GB/IP/ip_addresses";
        long count = uniqueIpCount(url); //Результат работы выводится в консоль
        System.out.println("Уникальных адресов: " + count);
    }
}
