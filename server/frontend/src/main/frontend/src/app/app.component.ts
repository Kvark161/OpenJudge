import {Component} from "@angular/core";
import {EskimoService} from "./services/eskimo.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  numberContests: number = -1;

  constructor(private eskimoService: EskimoService) {
    eskimoService.getContests().subscribe(contests => {
      this.numberContests = contests.length;
    });
  }

}
