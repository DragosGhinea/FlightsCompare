from playwright.sync_api import sync_playwright
from playwright_stealth import stealth_sync
from bs4 import BeautifulSoup
import time

def scrape_flight_data(page, url):
    print(f"Accessing URL: {url}")
    page.goto(url, wait_until="domcontentloaded")
    
    page.wait_for_timeout(5223)

    content = page.content()
    
    soup = BeautifulSoup(content, 'html.parser')
    print(soup.prettify())
    
    maps = [
        'flight-info__hour', 'origin-airport',
        'card-flight-num__content', 'flight_duration', 'destination-airport'
    ]
    
    results = []
    vals = []
    start = False
    
    for line in soup.prettify().splitlines():
        if 'flights-price-simple' in line:
            start = not start
            continue
        if start:
            if any(val in line for val in maps):
                vals.append(line.strip())
            if len(vals) == len(maps):
                results.append({
                    "departure_time": vals[0],
                    "origin_airport": vals[1],
                    "flight_number": vals[2],
                    "flight_duration": vals[3],
                    "destination_airport": vals[4],
                })
                vals = []
    
    return results

def main():
    _from = 'OTP'
    airports = [
        ("Birmingham", "BHX"),
        ("Bristol", "BRS"),
    ]
    
    with sync_playwright() as p:
        browser = p.firefox.launch(headless=True)
        page = browser.new_page()
        stealth_sync(page)
        
        for _, _to in airports:
            for year in (2024, 2025):
                for month in (11, 12, 1, 2, 3):
                    if month < 10: month = f"0{month}"
                    for day in range(1, 31):
                        if day < 10: day = f"0{day}"
                        
                        year, month, day = 2024, "12", "13"
                        
                        url = (f'https://www.eurowings.com/en/booking/flights/flight-search.html'
                               f'?isReward=false&destination={_to}&origin={_from}'
                               f'&source=web&origins={_from}&fromdate={year}-{month}-{day}'
                               f'&triptype=oneway&adults=1&children=0&infants=0&lng=en-GB#/book-flights/select')
                        
                        flight_data = scrape_flight_data(page, url)
                        for flight in flight_data:
                            print(f"Flight: {flight}")
                        
                        break
                    break
                break
            break
        
        browser.close()

if __name__ == "__main__":
    main()
