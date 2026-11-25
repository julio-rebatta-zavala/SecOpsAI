import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: "dateTime", standalone: true })
export class DateTimePipe implements PipeTransform {

   transform(v: string) {
    return v
      ? new Intl.DateTimeFormat("es-PE", {
          dateStyle: "short",
          timeStyle: "medium",
        }).format(new Date(v))
      : "";
  }
  
}
