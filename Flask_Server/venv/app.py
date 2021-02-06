import flask
from selenium import webdriver
from selenium.webdriver.common.by import By
app = flask.Flask(__name__)
driver = webdriver.Chrome('./chromedriver-Windows')
def amazon_shop():
    try:
        img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div[2]/div[4]/div[6]/div[3]/div/div[1]/div/div/div[2]/div[1]/div[1]/ul/li[1]/span/span/div/img")
    except:
        img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div[2]/div[4]/div[1]/div[2]/div[1]/div/div[1]/div/div/div[2]/div[1]/div[1]/ul/li[1]/span/span/div/img")
    return img_src

def flipkart_shop():
    try:
        img_src=driver.find_element(By.XPATH,"/html/body/div[1]/div/div[3]/div[1]/div[1]/div[1]/div/div[1]/div[2]/div[1]/div[2]/div/img")
    except:
        img_src=driver.find_element(By.XPATH,"/html/body/div[1]/div/div[3]/div[1]/div[1]/div[1]/div/div[1]/div[2]/div[1]/div[2]/img")
    return img_src
def myntra_shop():
    driver.find_element(By.XPATH,"/html/body/div[2]/div/div/div/main/div[2]/div[1]/div[1]/div/div[1]").click()
    try:
        img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div/div/div/main/div[2]/div[1]/div[7]/div/div[1]/img")
    except:
        try:
            img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div/div/div/main/div[2]/div[1]/div[6]/div/div[1]/img")
        except:
            try:
                img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div/div/div/main/div[2]/div[1]/div[8]/div/div[1]/img")
            except:
                img_src=driver.find_element(By.XPATH,"/html/body/div[2]/div/div/div/main/div[2]/div[1]/div[5]/div/div[1]/img")
    return img_src


@app.route('/<url>', methods=['GET', 'POST'])
def handle_request(url):
    url="https://"+url
    url=url.replace('+','/')
    site_type=url.split('.')[1]
    driver.get(url)
    if(site_type=="amazon"):
        img_src=amazon_shop()
    elif site_type=="flipkart":
        img_src=flipkart_shop()
    elif site_type=="myntra":
        img_src=myntra_shop()
    img_url=img_src.get_attribute("src")
    return img_url

app.run(host="192.168.0.108", port=5000)