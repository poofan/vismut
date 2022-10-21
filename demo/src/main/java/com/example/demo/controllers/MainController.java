package com.example.demo.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController
{
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	@GetMapping("/")
	public String getMainPage(Model model)
	{
		return "main.html";
	}
	
	@GetMapping("/main.html")
	public String getMainPage2(Model model)
	{
		return "main.html";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String downloadFile(@RequestParam("downloadFile") MultipartFile[] files, Model model)
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			for (MultipartFile file : files)
			{
				try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8")))
				{
					String line = br.readLine();

					while (line != null)
					{
						if (!line.startsWith("="))
						{
							String[] arrLine = line.split(";");

							for (int i = 0; i < arrLine.length; i++)
							{
								if (i == 0 || i == 7 || i == 8)
								{
									if (i == 7)
									{
										String personalNumber = arrLine[i].substring(arrLine[i].length() - 3);
										String format = "[^\\d]";

										String personalNumberF = personalNumber.replaceAll(format, "");

										sb.append(";").append(personalNumberF).append(";");
									}
									else
									{
										if (i == 0)
										{
											sb.append(arrLine[i]);
										}
										else if (i == 8)
										{
											sb.append(arrLine[i] + "\n");
										}
									}

								}
							}
						}

						line = br.readLine();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (!sb.toString().startsWith(";"))
			{
				StringBuilder fileName = new StringBuilder();
				fileName.append("Платёжки_").append(System.currentTimeMillis());
				
				try (FileWriter writer = new FileWriter("C:/Обработанные платёжки Висмут/" + fileName.toString() + ".txt", false))
				{
					writer.write(sb.toString());

					writer.flush();

					System.out.println("Успешно записали");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(Exception e)
		{
			model.addAttribute("exceptionMsg", "Ошибка при обработке файла(ов): " + e.toString());
			LOG.error(e.toString());
			
			return "main.html";
		}		
		
		return "successMain.html";
	}

}
