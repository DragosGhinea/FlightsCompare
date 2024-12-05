from playwright.sync_api import sync_playwright
from playwright_stealth import stealth_sync
from bs4 import BeautifulSoup



def scrape_flight_data(page, url):
    print(f"Accessing URL: {url}")
    page.goto(url, wait_until="domcontentloaded")

    page.wait_for_selector('.o-flight-select', timeout=15231)

    page.wait_for_timeout(2314)

    content = page.content()
    
    soup = BeautifulSoup(content, 'html.parser')

    flight_cards = soup.find_all('div', class_='o-flight-card')

    # List to store flight information dictionaries
    flights_info = []

    # Loop through each flight card
    for card in flight_cards:
        flight_info = {}

        # Extract origin and destination
        origin = card.find('div', class_='o-flight-item__text').text.strip()
        destination = card.find_all('div', class_='o-flight-item__text')[-1].text.strip()

        # Extract times for departure and arrival
        times = card.find_all('div', class_='o-flight-item__title')
        departure_time = times[0].text.strip()
        arrival_time = times[-1].text.strip()

        # Extract stop information
        stop_info = card.find('div', class_='o-flight-item__text-s').text.strip()

        # Extract flight fare details
        fares = []
        fare_items = card.find_all('article', class_='flight-tariff')
        for fare in fare_items:
            fare_type = fare.find('h4', class_='flight-tariff-module__type--XiCoI').text.strip()

            fare_price_element = fare.find('span', class_='farePriceSelect-module__priceVal--lBLXC')
            if fare_price_element:
                fare_price = fare_price_element.text.strip()
            else:
                fare_price = "N/A"

            fare_availability_element = fare.find('p', class_='farePriceSelect-module__availability--qLGXV')
            if fare_availability_element:
                fare_availability = fare_availability_element.text.strip()
            else:
                fare_availability = "N/A"
            
            fares.append({
                'type': fare_type,
                'price': fare_price,
                'availability': fare_availability
            })

        flight_info['origin'] = origin
        flight_info['destination'] = destination
        flight_info['departure_time'] = departure_time
        flight_info['arrival_time'] = arrival_time
        flight_info['stop_info'] = stop_info
        flight_info['fares'] = fares

        # Add the current flight's info to the list
        flights_info.append(flight_info)

    # Output the list of all extracted flight information
    print(flights_info)

def main():
    _from = 'OTP'
    airports = [
        ("Birmingham", "BHX"),
        ("Bristol", "BRS"),
    ]
    
    with sync_playwright() as p:
        browser = p.webkit.launch(headless=True)
        context = browser.new_context()
        page = context.new_page()
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
