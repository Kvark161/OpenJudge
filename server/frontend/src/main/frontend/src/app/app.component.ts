import {Component} from "@angular/core";
import {EskimoService} from "./services/eskimo.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

    serverTime: string = '';

    constructor(private eskimoService: EskimoService) {
        this.eskimoService.getServerTime().subscribe(serverTime => this.serverTime = serverTime);
    }

}
