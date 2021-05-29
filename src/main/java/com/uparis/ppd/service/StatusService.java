package com.uparis.ppd.service;

import com.uparis.ppd.exception.StorageException;
import com.uparis.ppd.model.Status;
import com.uparis.ppd.repository.StatusRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;


    public Status create(boolean admin, boolean superAdmin) {
        Status status = new Status(admin, superAdmin);
        update(status);
        return status;
    }

    public void update(Status status) {
        statusRepository.save(status);
    }

    public List<Status> createFromFile(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            List<Status> status = new ArrayList<>();
            for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
                if (index > 0) {
                    XSSFRow row = worksheet.getRow(index);
                    status.add(create(Boolean.parseBoolean(row.getCell(9).getStringCellValue()), false));
                }
            }
            return status;
        } catch (IOException e) {
            throw new StorageException("impossible de lire le fichier " + file, e);
        }
    }
}
